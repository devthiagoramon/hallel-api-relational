package br.hallel.relational.api.app.payment.checkout_transparent.service;

import br.hallel.relational.api.app.association.exception.AssociateException;
import br.hallel.relational.api.app.association.model.Associate;
import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import br.hallel.relational.api.app.association.model.AssociationPayment;
import br.hallel.relational.api.app.association.repository.AssociatePaymentRepository;
import br.hallel.relational.api.app.association.repository.AssociateRepository;
import br.hallel.relational.api.app.email.dto.EmailParticipationDTO;
import br.hallel.relational.api.app.email.service.EmailEventParticipationService;
import br.hallel.relational.api.app.event.dto.PaymentStatusDTO;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentFood;
import br.hallel.relational.api.app.event.model.enum_type.TransactionType;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.event.repository.FoodRepository;
import br.hallel.relational.api.app.event.repository.FoodTransactionRepository;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.ProcessNotificationResponseDTO;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executor;

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
    private final AssociateRepository associateRepository;
    private final AssociatePaymentRepository associatePaymentRepository;
    private final EmailEventParticipationService emailEventParticipationService;
    private final Executor clientInboundChannelExecutor;

    @Transactional
    public ProcessNotificationResponseDTO processNotification(Long paymentId) throws MPException, MPApiException {

        Optional<EventParticipation> optionalParticipation = eventParticipationRepository.findByMercadoPagoPaymentId(
                paymentId);

        if (optionalParticipation.isPresent()) {
            Payment payment = mercadoPagoClient.getPaymentStatus(paymentId);
            return processEventNotification(payment, optionalParticipation.get());
        }

        Optional<FoodTransaction> optionalFoodTransaction = foodTransactionRepository.findByMercadoPagoPaymentId(
                paymentId);
        if (optionalFoodTransaction.isPresent()) {
            Payment payment = mercadoPagoClient.getPaymentStatus(paymentId);
            return processFoodNotification(optionalFoodTransaction.get(), payment);
        }

        Optional<AssociationPayment> optionalPayment =
                this.associatePaymentRepository.findByMercadoPagoPaymentId(paymentId);

        if (optionalPayment.isPresent()) {
            AssociationPayment paymentTransaction = optionalPayment.get();
            Associate associate = paymentTransaction.getAssociate();

            Payment payment = mercadoPagoClient.getPaymentStatus(paymentId);

            return processAssociatePayment(associate, payment);
        }

        log.warn("Nenhuma participação de evento ou transação de alimento encontrada para o ID de pagamento: {}",
                paymentId);
        return new ProcessNotificationResponseDTO(false, "not_found");
    }

    @Transactional
    public ProcessNotificationResponseDTO processAssociatePayment(Associate associate, Payment payment) {
        log.info("Processing association payment webhook for paymentId: {}", payment.getId());

        String paymentStatus = payment.getStatus();
        boolean success = false;
        LocalDateTime paymentTime = LocalDateTime.now(ZoneOffset.UTC); // Garante fuso horário consistente


        AssociationPayment paymentTransaction = associatePaymentRepository
                .findByMercadoPagoPaymentId(payment.getId())
                .orElseThrow(() -> new AssociateException("Associate Payment transaction not found for Mercado Pago ID: " + payment.getId()));

        // 2. Mapeamento e Atualização dos STATUS
        switch (paymentStatus) {
            case "approved":
                paymentTransaction.setStatus(AssociatePaymentStatus.PAGO);
                paymentTransaction.setPaidDate(paymentTime);

                updateAssociateRenewal(associate, paymentTransaction.getMonthsCovered(), paymentTime);

                associate.setStatus(AssociatePaymentStatus.PAGO);
                success = true;

                template.convertAndSend("/topic/payments/" + associate.getUser().getId(),
                        new PaymentStatusDTO(null, null, StatusPaymentEventParticipation.PAGO));
                break;

            case "pending":
                paymentTransaction.setStatus(AssociatePaymentStatus.PENDENTE);
                break;

            case "rejected":
            case "cancelled":
            case "refunded":
            case "charged_back":
            case "in_mediation":
                paymentTransaction.setStatus(AssociatePaymentStatus.REJEITADO);

                // Se o pagamento for rejeitado, a associação (se estiver pendente) deve ser suspensa.
                if (associate.getStatus() == AssociatePaymentStatus.PENDENTE || associate.getStatus() == AssociatePaymentStatus.PAGO_ATRASADO) {
                    associate.setStatus(AssociatePaymentStatus.SUSPENSO);
                }
                break;

            default:
                log.warn("Status de pagamento desconhecido: {}", paymentStatus);
                paymentTransaction.setStatus(AssociatePaymentStatus.ERRO);
                break;
        }

        // 3. Salva AMBAS as entidades
        associatePaymentRepository.save(paymentTransaction);
        associateRepository.save(associate);

        return new ProcessNotificationResponseDTO(success, paymentStatus);
    }

    /**
     * Lógica para calcular a próxima data de renovação no Associate (movida do método anterior).
     * Esta lógica deve existir no seu ProcessPaymentNotificationService.
     */
    private void updateAssociateRenewal(Associate associate, int monthsCovered, LocalDateTime paymentTime) {
        LocalDateTime currentRenewal = associate.getRenewalDate();

        if (associate.getAssociateSince() == null) {
            // 1. Primeiro pagamento de adesão
            associate.setAssociateSince(paymentTime);
            associate.setRenewalDate(paymentTime.plusMonths(monthsCovered));

        } else if (currentRenewal == null || currentRenewal.isBefore(paymentTime)) {
            // 2. Associação estava inativa/vencida (Reativação). Conta a partir de AGORA.
            associate.setRenewalDate(paymentTime.plusMonths(monthsCovered));

        } else {
            // 3. Renovação antecipada. Adiciona o tempo no final do período atual.
            associate.setRenewalDate(currentRenewal.plusMonths(monthsCovered));
        }
    }

    @Transactional
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
                template.convertAndSend("/topic/payments/" + externalReferenceId,
                        new PaymentStatusDTO(null, null, StatusPaymentEventParticipation.NAO_PAGO));
                break;
            default:
                log.warn("Status de pagamento desconhecido: {}", paymentStatus);
                participationStatus = StatusPaymentEventParticipation.PENDENTE;
                break;
        }
        participation.setStatusPaymentEventParticipation(participationStatus);

        if ("approved".equalsIgnoreCase(paymentStatus)) {
            EventTransaction newTransaction = new EventTransaction();
            try {


                newTransaction.setEvent(participation.getEvent());
                newTransaction.setDescription(
                        "Pagamento de ingresso para o evento: " + participation.getEvent().getTitle());
                newTransaction.setTransactionType(TransactionType.ENTRADA);

                Double liquidValue = generateLiquidValue(participation.getMercadoPagoPaymentId());
                newTransaction.setValue(liquidValue);
                newTransaction.setDateTransaction(new Date());
                log.warn("VALOR LIQUIDO RECEBIDO DO MERCADO PAGO: {}", liquidValue);

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
            participation.setAmountPaid(amountPaid.doubleValue());
            log.info("Transação e participação de evento atualizadas para: {}. Event ID: {}", paymentStatus,
                    participation.getEvent().getId());

            template.convertAndSend("/topic/payments/" + externalReferenceId,
                    new PaymentStatusDTO(null, null, StatusPaymentEventParticipation.PAGO));

            EmailParticipationDTO emailDto = new EmailParticipationDTO(
                    participation.getEmail(),
                    participation.getName(),
                    participation.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    participation.getEvent().getTitle()
            );
            emailEventParticipationService.sendComprovantEventParticipation(
                    emailDto, participation.getEvent().getId().toString(),
                    participation.getEvent().getWhatsAppGroupLink()
            );
        }

        eventParticipationRepository.save(participation);

        return new ProcessNotificationResponseDTO(success, paymentStatus);
    }

    @Transactional
    public ProcessNotificationResponseDTO processFoodNotification(FoodTransaction foodTransaction, Payment payment) {
        log.info("Processing food payment webhook for transactionId: {}", foodTransaction.getId());

        String paymentStatus = payment.getStatus();
        String externalReferenceId = payment.getExternalReference();

        StatusPaymentFood participationStatus;
        boolean success = false;

        switch (paymentStatus) {
            case "approved":
                participationStatus = StatusPaymentFood.PAGO;
                success = true;
                break;
            case "pending":
                participationStatus = StatusPaymentFood.PENDENTE;
                break;
            case "rejected":
            case "cancelled":
            case "refunded":
            case "charged_back":
            case "in_mediation":
                participationStatus = StatusPaymentFood.NAO_PAGO;
                break;
            default:
                log.warn("Status de pagamento desconhecido: {}", paymentStatus);
                participationStatus = StatusPaymentFood.PENDENTE;
                break;
        }
        foodTransaction.setStatus(participationStatus);
        if ("approved".equalsIgnoreCase(paymentStatus)) {
            try {

                log.info("STEP 1: Preparing entities before database save...");
                Double liquidValue = generateLiquidValue(foodTransaction.getMercadoPagoPaymentId());
                EventTransaction eventTransaction = new EventTransaction();
                eventTransaction.setDescription("Venda de Alimento Confirmada: " + foodTransaction.getDescription());
                eventTransaction.setValue(liquidValue);
                eventTransaction.setTransactionType(TransactionType.ENTRADA);
                eventTransaction.setEvent(foodTransaction.getEvent());
                eventTransaction.setDateTransaction(new Date());
                EventTransaction eventTransaction1 = eventTransactionRepository.save(eventTransaction);
                foodTransaction.setEventTransaction(eventTransaction1);

                for (FoodSaleItem item : foodTransaction.getSaleItems()) {
                    Foods food = item.getFood();
                    if (food != null) {
                        int newStock = food.getStockQuantity() - item.getQuantity();
                        food.setStockQuantity(newStock);
                    }
                }
                foodTransaction.setStatus(StatusPaymentFood.PAGO);
                foodTransaction.setDateTransaction(OffsetDateTime.now(ZoneId.of("America/Manaus")));
                foodTransactionRepository.save(foodTransaction);
                log.info("EVENT TRANSACTION CRIADO " + eventTransaction1.getId().toString());

                template.convertAndSend("/topic/payments/" + externalReferenceId,
                        new PaymentStatusDTO(null, null, StatusPaymentEventParticipation.PAGO));

                log.info("PROCESS COMPLETE: Food sale for transaction {} finished successfully.",
                        foodTransaction.getId());
                return new ProcessNotificationResponseDTO(success, paymentStatus);

            } catch (Exception e) {
                log.error("CRITICAL ERROR while processing food notification for transaction {}: {}",
                        foodTransaction.getId(), e.getMessage(), e);
                return new ProcessNotificationResponseDTO(false, "error");
            }
        }
        log.warn("Nenhuma participação de evento ou transação de alimento encontrada para o ID de pagamento: {}",
                payment.getId());
        return new ProcessNotificationResponseDTO(false, "not_found");
    }

    private Double generateLiquidValue(Long mercadoPagoPaymentId) throws MPException, MPApiException {
        PaymentClient client = new PaymentClient();
        Payment mpPayment = client.capture(mercadoPagoPaymentId);
        Double valorLiquido = mpPayment.getTransactionDetails().getNetReceivedAmount().doubleValue();
        return valorLiquido;
    }
}
