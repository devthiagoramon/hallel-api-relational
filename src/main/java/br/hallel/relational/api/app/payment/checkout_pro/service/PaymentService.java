package br.hallel.relational.api.app.payment.checkout_pro.service;

import br.hallel.relational.api.app.payment.checkout_pro.dto.CreateChargeRequest;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public PaymentService() {
        MercadoPagoConfig.setAccessToken("");
    }

    public Payment createPixPayment(CreateChargeRequest dto) throws MPException, MPApiException {
        PaymentClient client = new PaymentClient();

        // Dados do pagador (informações necessárias para o Pix).
        // Estas informações são obrigatórias para a criação de um pagamento Pix.
        // Você deve substituí-las por dados dinâmicos do seu sistema.
        PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                .email(dto.email()) // E-mail do cliente
                .firstName(dto.firstName()) // Nome do cliente
                .lastName(dto.lastName()) // Sobrenome do cliente
                .identification(
                        IdentificationRequest.builder()
                                .type("CPF") // Tipo de documento (CPF, CNPJ, etc.)
                                .number(dto.cpf()) // Número do documento
                                .build())
                .build();

        // Constrói a requisição para criar o pagamento.
        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(dto.amount()) // Valor da transação
                .description(dto.description()) // Descrição da compra
                .paymentMethodId("pix") // A chave para definir que o pagamento será via Pix.
                .payer(payerRequest) // Adiciona os dados do pagador à requisição.
                .build();

        // Envia a requisição e cria o pagamento na API do Mercado Pago.
        Payment payment = client.create(createRequest);

        return payment;
    }
}