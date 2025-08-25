package br.hallel.relational.api.app.payment.checkout_transparent.client;


import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreatePixPaymentRequestDTO;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentPointOfInteraction;
import com.mercadopago.resources.payment.PaymentTransactionData;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MercadoPagoClient {

    //@Value("${mercadopago.access.token}")
    private String accessToken = "APP_USR-1199359181048411-081921-fbdca99980d31c83bf2f7b61add671b8-2640484802";

    @PostConstruct
    public void init() {
        log.info("Iniciando Mercado Pago");
        // Inicializa a SDK. Lembre-se, isso deve ser feito uma vez só.
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public Payment createPixPayment(CreatePixPaymentRequestDTO dto) throws MPException, MPApiException {
        log.info("Criando pagamento Pix no Mercado Pago com dados: {}", dto);

        // Cria o cliente de pagamento.
        PaymentClient client = new PaymentClient();

        // Dados do pagador, necessários para o Pix.
        PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                .email(dto.payerEmail())
                .firstName(dto.payerFirstName())
                .lastName(dto.payerLastName())
                .identification(
                        IdentificationRequest.builder()
                                .type("CPF") // Assumindo CPF, mas você pode parametrizar
                                .number(dto.payerIdentificationNumber())
                                .build())
                .build();

        // Constrói a requisição para o pagamento Pix.
        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(dto.amount())
                .description(dto.description())
                .paymentMethodId("pix")
                .payer(payerRequest)
                .build();

        // Envia a requisição e cria o pagamento na API do Mercado Pago.
        Payment payment = client.create(createRequest);
        log.info("Pagamento Pix criado com sucesso no Mercado Pago");

        return payment;
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

        // Seu método para buscar o status do pagamento
        Payment payment = getPaymentStatus(paymentId);

        // Acessando as informações de comprovante
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
}