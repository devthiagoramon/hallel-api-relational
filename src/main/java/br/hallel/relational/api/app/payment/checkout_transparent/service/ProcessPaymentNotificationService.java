package br.hallel.relational.api.app.payment.checkout_transparent.service;

import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.ProcessNotificationResponseDTO;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ProcessPaymentNotificationService {

    private final MercadoPagoClient mercadoPagoClient;
    private final EventParticipationRepository eventParticipationRepository;

    @Transactional
    public ProcessNotificationResponseDTO processNotification(Long paymentId) throws MPException, MPApiException {
        // 1. Busca o status do pagamento na API do Mercado Pago
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

        // 3. Atualiza o status da participação no banco de dados
        Optional<EventParticipation> optionalParticipation = eventParticipationRepository.findByPixTxid(payment.getExternalReference());
        if (optionalParticipation.isPresent()) {
            EventParticipation participation = optionalParticipation.get();
            participation.setStatusPaymentEventParticipation(participationStatus);
            eventParticipationRepository.save(participation);
            log.info("Status da participação de evento atualizado para: {}. EventParticipation ID: {}", participationStatus, participation.getId());
        } else {
            log.warn("Participação de evento não encontrada para o PixTxid/ExternalReference: {}", payment.getExternalReference());
        }

        return new ProcessNotificationResponseDTO(success, paymentStatus);
    }
}
