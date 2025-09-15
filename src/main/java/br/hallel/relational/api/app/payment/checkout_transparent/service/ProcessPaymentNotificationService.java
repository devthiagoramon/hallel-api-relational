package br.hallel.relational.api.app.payment.checkout_transparent.service;

import br.hallel.relational.api.app.event.dto.PaymentStatusDTO;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.event.repository.FoodRepository;
import br.hallel.relational.api.app.event.repository.FoodTransactionRepository;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.ProcessNotificationResponseDTO;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ProcessPaymentNotificationService {

    private final MercadoPagoClient mercadoPagoClient;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventTransactionRepository eventTransactionRepository;
    private final SimpMessagingTemplate template;
    private final FoodTransactionRepository foodTransactionRepository;
    private final FoodRepository foodRepository;

    @Transactional
    public ProcessNotificationResponseDTO processNotification(Long paymentId) throws MPException, MPApiException {
        Optional<EventParticipation> optionalParticipation = eventParticipationRepository.findByMercadoPagoPaymentId(paymentId);

        if (optionalParticipation.isPresent()) {
            // Se encontrarmos, passamos o objeto e o paymentId para o método de evento
            Payment payment = mercadoPagoClient.getPaymentStatus(paymentId);
            return processEventNotification(payment, optionalParticipation.get());
        }

        // Se não, tentamos encontrar uma transação de alimento com este paymentId
        Optional<FoodTransaction> optionalFoodTransaction = foodTransactionRepository.findByMercadoPagoPaymentId(paymentId);
        if (optionalFoodTransaction.isPresent()) {
            return processFoodNotification(optionalFoodTransaction.get());
        }

        log.warn("Nenhuma participação de evento ou transação de alimento encontrada para o ID de pagamento: {}", paymentId);
        return new ProcessNotificationResponseDTO(false, "not_found");
    }

    @Transactional // Adicionando @Transactional para garantir a consistência
    public ProcessNotificationResponseDTO processEventNotification(Payment payment, EventParticipation participation) {
        log.info("Processing event payment webhook for paymentId: {}", payment.getId());

        String paymentStatus = payment.getStatus();
        BigDecimal amountPaid = payment.getTransactionAmount();
        String externalReferenceId = payment.getExternalReference();

        StatusPaymentEventParticipation participationStatus;
        boolean success = false;

        switch (paymentStatus) {
            case "approved":
                participationStatus = StatusPaymentEventParticipation.PAGO;
                success = true;
                break;
            case "pending":
                participationStatus = StatusPaymentEventParticipation.PENDENTE;
                break;
            case "rejected":
            case "cancelled":
            case "refunded":
            case "charged_back":
            case "in_mediation":
                participationStatus = StatusPaymentEventParticipation.NAO_PAGO;
                break;
            default:
                log.warn("Status de pagamento desconhecido: {}", paymentStatus);
                participationStatus = StatusPaymentEventParticipation.PENDENTE;
                break;
        }
        participation.setStatusPaymentEventParticipation(participationStatus);

        if ("approved".equalsIgnoreCase(paymentStatus)) {

            EventTransaction newTransaction = new EventTransaction();
            newTransaction.setEvent(participation.getEvent());
            newTransaction.setDescription("Pagamento de ingresso para o evento: " + participation.getEvent().getTitle());
            newTransaction.setTransactionType(TransactionType.ENTRADA);
            newTransaction.setValue(Double.parseDouble(amountPaid.toString()));
            newTransaction.setDateTransaction(new Date());
            try {
                newTransaction.setReceiptPaymentFileImage(mercadoPagoClient.getPixReceiptUrl(payment.getId()));
            } catch (MPException e) {
                throw new RuntimeException(e);
            } catch (MPApiException e) {
                throw new RuntimeException(e);
            }
            newTransaction.setMercadoPagoPaymentId(payment.getId());
            newTransaction.setIsEditable(false);
            eventTransactionRepository.save(newTransaction);
            participation.setPaidDate(Instant.now().atOffset(ZoneOffset.UTC));
            participation.setAmountPaid(Double.parseDouble(amountPaid.toString()));
            log.info("Transação e participação de evento atualizadas para: {}. Event ID: {}", paymentStatus,

                    participation.getEvent().getId());

            template.convertAndSend("/topic/payments/" + externalReferenceId,
                    new PaymentStatusDTO(null, null, StatusPaymentEventParticipation.PAGO));
        }

        eventParticipationRepository.save(participation);

        return new ProcessNotificationResponseDTO(success, paymentStatus);
    }

    @Transactional
    public ProcessNotificationResponseDTO processFoodNotification(FoodTransaction transaction) {
        log.info("Processing food payment webhook for transactionId: {}", transaction.getId());

        try {
            if (transaction.getStatus() == StatusPaymentFood.PAGO) {
                log.warn("Transaction already had PAGO status. Ignoring duplicate notification. ID: {}", transaction.getId());
                return new ProcessNotificationResponseDTO(true, "approved");
            }

            // ETAPA 1: PREPARAR AS ENTIDADES
            log.info("STEP 1: Preparing entities before database save...");
            EventTransaction eventTransaction = new EventTransaction();
            eventTransaction.setDescription("Venda de Alimento Confirmada: " + transaction.getDescription());
            eventTransaction.setValue(transaction.getValue());
            eventTransaction.setTransactionType(TransactionType.ENTRADA);
            eventTransaction.setEvent(transaction.getEvent());
            eventTransaction.setDateTransaction(new Date());
            transaction.setEventTransaction(eventTransaction);

            for (FoodSaleItem item : transaction.getSaleItems()) {
                Foods food = item.getFood();
                if (food != null) {
                    int newStock = food.getStockQuantity() - item.getQuantity();
                    food.setStockQuantity(newStock);
                    // Não precisa de save aqui, o Cascade vai cuidar
                }
            }
            transaction.setStatus(StatusPaymentFood.PAGO);
            transaction.setDateTransaction(OffsetDateTime.now(ZoneId.of("America/Manaus")));
            log.info("STEP 1: Entities prepared successfully.");

            // ETAPA 2: SALVAR NO BANCO DE DADOS
            log.info("STEP 2: Saving transaction to the database...");
            foodTransactionRepository.save(transaction);
            log.info("STEP 2: Transaction saved successfully.");

            // ETAPA 3: ENVIAR MENSAGEM WEBSOCKET
            log.info("STEP 3: Preparing to send WebSocket message to topic /topic/payments/{}", transaction.getId().toString());

            Map<String, String> payload = new HashMap<>();
            payload.put("statusPaymentEventParticipation", "PAGO");

            template.convertAndSend("/topic/payments/" + transaction.getId().toString(), payload);
            log.info("STEP 3: WebSocket message sent successfully.");

            log.info("PROCESS COMPLETE: Food sale for transaction {} finished successfully.", transaction.getId());
            return new ProcessNotificationResponseDTO(true, "approved");

        } catch (Exception e) {
            log.error("CRITICAL ERROR while processing food notification for transaction {}: {}", transaction.getId(), e.getMessage(), e);
            return new ProcessNotificationResponseDTO(false, "error");
        }
    }
}
