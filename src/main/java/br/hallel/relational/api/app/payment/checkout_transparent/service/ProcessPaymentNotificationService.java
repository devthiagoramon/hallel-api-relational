package br.hallel.relational.api.app.payment.checkout_transparent.service;

import br.hallel.relational.api.app.event.dto.PaymentStatusDTO;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.EventTransaction;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.TransactionType;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
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
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ProcessPaymentNotificationService {

    private final MercadoPagoClient mercadoPagoClient;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventTransactionRepository eventTransactionRepository;
    private final SimpMessagingTemplate template;

    @Transactional
    public ProcessNotificationResponseDTO processNotification(Long paymentId) throws MPException, MPApiException {
        // 1. Busca o status completo do pagamento na API do Mercado Pago
        Payment payment = mercadoPagoClient.getPaymentStatus(paymentId);
        String paymentStatus = payment.getStatus();
        BigDecimal amountPaid = payment.getTransactionAmount();
        String externalReferenceId = payment.getExternalReference();

        // 2. Mapeia o status do Mercado Pago para o status da sua aplicação
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

        // 3. Busca a EventParticipation no banco de dados usando o ID do pagamento
        Optional<EventParticipation> optionalParticipation =
                eventParticipationRepository.findByMercadoPagoPaymentId(paymentId);

        if (optionalParticipation.isPresent()) {
            EventParticipation participation = optionalParticipation.get();
            participation.setStatusPaymentEventParticipation(participationStatus);

            // 4. Se o pagamento for aprovado, cria a transação e atualiza a participação
            if ("approved".equalsIgnoreCase(paymentStatus)) {

                // Cria a EventTransaction SOMENTE se o pagamento for aprovado
                EventTransaction newTransaction = new EventTransaction();
                newTransaction.setEvent(participation.getEvent());
                newTransaction.setDesciption(
                        "Pagamento de ingresso para o evento: " + participation.getEvent().getTitle());
                newTransaction.setTransactionType(TransactionType.ENTRADA);
                newTransaction.setValue(Double.parseDouble(amountPaid.toString()));
                newTransaction.setDateTransaction(new Date());
                newTransaction.setReceiptPaymentFileImage(mercadoPagoClient.getPixReceiptUrl(paymentId));
                newTransaction.setMercadoPagoPaymentId(payment.getId());
                newTransaction.setIsEditable(false);
                eventTransactionRepository.save(newTransaction);

                participation.setPaidDate(Instant.now().atOffset(ZoneOffset.UTC));
                participation.setAmountPaid(Double.parseDouble(amountPaid.toString()));
                log.info("Transação e participação de evento atualizadas para: {}. Event ID: {}", paymentStatus,
                        participation.getEvent().getId());

                template.convertAndSend("/topic/payments/" + externalReferenceId,
                        new PaymentStatusDTO(null, null,
                                StatusPaymentEventParticipation.PAGO));
            }

            // Salva a entidade de participação atualizada
            eventParticipationRepository.save(participation);
        } else {
            log.warn("Participação de evento não encontrada para o ID de pagamento: {}", paymentId);
        }

        return new ProcessNotificationResponseDTO(success, paymentStatus);
    }
}
