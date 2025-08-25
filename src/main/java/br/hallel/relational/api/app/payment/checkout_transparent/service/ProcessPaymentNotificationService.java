package br.hallel.relational.api.app.payment.checkout_transparent.service;

import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.EventTransaction;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ProcessPaymentNotificationService {

    private final MercadoPagoClient mercadoPagoClient;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventTransactionRepository eventTransactionRepository;

    @Transactional
    public ProcessNotificationResponseDTO processNotification(Long paymentId) throws MPException, MPApiException {
        // 1. Busca o status completo do pagamento na API do Mercado Pago
        Payment payment = mercadoPagoClient.getPaymentStatus(paymentId);
        String paymentStatus = payment.getStatus();

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

        // 3. Busca a EventTransaction no banco de dados usando o ID do pagamento do Mercado Pago
        Optional<EventTransaction> optionalTransaction =
                eventTransactionRepository.findByMercadoPagoPaymentId(paymentId);

        if (optionalTransaction.isPresent()) {
            EventTransaction transaction = optionalTransaction.get();

            // 4. Busca a participação do evento correspondente ao usuário e ao evento da transação
            Optional<EventParticipation> optionalParticipation =
                    eventParticipationRepository.findByMercadoPagoPaymentId(paymentId);

            if (optionalParticipation.isPresent()) {
                EventParticipation participation = optionalParticipation.get();
                participation.setStatusPaymentEventParticipation(participationStatus);

                // 5. Se o pagamento for aprovado, atualiza a transação com a URL do comprovante e a participação com a data de pagamento
                if ("approved".equalsIgnoreCase(paymentStatus)) {
                    String receiptUrl = mercadoPagoClient.getPixReceiptUrl(paymentId);
                    transaction.setReceiptPaymentFileImage(receiptUrl);
                    participation.setPaidDate(Instant.now().atOffset(ZoneOffset.UTC));
                }

                // Salva as entidades atualizadas
                eventTransactionRepository.save(transaction);
                eventParticipationRepository.save(participation);

                log.info("Transação e participação de evento atualizadas para: {}. Transaction ID: {}", paymentStatus, transaction.getId());
            } else {
                log.warn("Participação de evento não encontrada para a transação com ID: {}", transaction.getId());
            }
        } else {
            log.warn("Transação de evento não encontrada para o ID de pagamento: {}", paymentId);
        }

        return new ProcessNotificationResponseDTO(success, paymentStatus);
    }
}
