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
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MercadoPagoClient {

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
}