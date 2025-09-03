package br.hallel.relational.api.app.payment.checkout_transparent.client;

import br.hallel.relational.api.app.event.exception.PaymentRefundException;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreatePixPaymentRequestDTO;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.*;
import com.mercadopago.example.apis.order.RefundTotal;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentPointOfInteraction;
import com.mercadopago.resources.payment.PaymentRefund;
import com.mercadopago.resources.payment.PaymentTransactionData;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class MercadoPagoClient {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.notification.url}")
    private String notificationUrl;

    @PostConstruct
    public void init() {
        log.info("Iniciando Mercado Pago");
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public Payment createPixPayment(CreatePixPaymentRequestDTO dto, UUID userId) throws MPException, MPApiException {
        log.info("Criando pagamento Pix no Mercado Pago com dados: {}", dto);

        String externalReference = userId.toString();

        PaymentClient client = new PaymentClient();

        PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                .email(dto.payerEmail())
                .firstName(dto.payerFirstName())
                .lastName(dto.payerLastName())
                .identification(
                        IdentificationRequest.builder()
                                .type("CPF")
                                .number(dto.payerIdentificationNumber())
                                .build())
                .build();

        // CORREÇÃO: Removendo o PaymentAdditionalInfoRequest, pois shipments
        // deve ser definido no nível superior.

        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(dto.amount())
                .description(dto.description())
                .paymentMethodId("pix")
                .payer(payerRequest)
                .externalReference(externalReference)
                .notificationUrl(notificationUrl)
                .build();

        Payment payment = client.create(createRequest);
        log.info("Pagamento Pix criado com sucesso no Mercado Pago");

        return payment;
    }

    public String getPaymentQRCode(long paymentId) throws MPException, MPApiException {
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);

        return payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
    }

    public Payment getPaymentStatus(long paymentId) throws MPException, MPApiException {
        log.info("Buscando status do pagamento Pix com ID: {}", paymentId);
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);
        log.info("Status do pagamento Pix obtido com sucesso: {}", payment.getStatus());
        return payment;
    }

    public String getPixReceiptUrl(long paymentId) throws MPException, MPApiException {
        log.info("Buscando comprovante do pagamento Pix com ID: {}", paymentId);

        Payment payment = getPaymentStatus(paymentId);

        if (payment != null && "approved".equals(payment.getStatus())) {
            PaymentPointOfInteraction pointOfInteraction = payment.getPointOfInteraction();
            if (pointOfInteraction != null) {
                PaymentTransactionData transactionData = pointOfInteraction.getTransactionData();
                if (transactionData != null) {
                    String ticketUrl = transactionData.getTicketUrl();
                    if (ticketUrl != null) {
                        return ticketUrl;
                    }
                }
            }
        }
        log.warn("Comprovante não encontrado ou pagamento não aprovado para o ID: {}", paymentId);
        return null;
    }

    public boolean requestRefund(Long paymentId, Double amount) throws PaymentRefundException {
        log.info("Attempting to refund paymentId: {} for amount R${}", paymentId, amount);

        PaymentRefundClient client = new PaymentRefundClient();

        try {
            PaymentRefund refund = client.refund(paymentId);

            if (refund.getId() != null) {
                log.info("Refund successfully processed for paymentId: {}. Refund ID: {}", paymentId, refund.getId());
                return true;
            } else {
                log.error("Refund failed for paymentId: {}. No refund ID returned.", paymentId);
                throw new PaymentRefundException("Mercado Pago API did not return a refund ID.");
            }
        } catch (MPApiException e) {
            log.error("Mercado Pago API error while refunding paymentId {}. Status: {}, Content: {}",
                    paymentId, e.getApiResponse().getStatusCode(), e.getApiResponse().getContent());
            throw new PaymentRefundException("Mercado Pago API Error: " + e.getApiResponse().getContent());
        } catch (MPException e) {
            log.error("Mercado Pago SDK error while refunding paymentId {}. Message: {}", paymentId, e.getMessage());
            throw new PaymentRefundException("Mercado Pago SDK Error: " + e.getMessage());
            // Retorne false para indicar que o reembolso não foi bem-sucedido
        }
    }
}
