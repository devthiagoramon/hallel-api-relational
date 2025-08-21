package br.hallel.relational.api.app.payment.checkout_transparent.controller;


import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreatePixPaymentRequestDTO;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/public/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final MercadoPagoClient client;

    @PostMapping("/create-pix")
    @Operation(summary = "Cria uma cobrança pix" +
            "precisa das informações do cliente que vai realizar o pagamento")
    public ResponseEntity<Map<String, String>> createPixPayment(@Valid @RequestBody CreatePixPaymentRequestDTO requestDTO) {
        log.info("Creating a Pix payment...");
        try {
            Payment payment = client.createPixPayment(requestDTO);

            // Extrai as informações do QR Code e do Pix 'copia e cola' da resposta.
            String pixCode = payment.getPointOfInteraction().getTransactionData().getQrCode();
            String qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();

            // Retorna as informações para o seu frontend
            return ResponseEntity.ok(Map.of(
                    "pixCode", pixCode,
                    "qrCodeBase64", qrCodeBase64
            ));
        } catch (MPException | MPApiException e) {
            log.error("Failed to create Pix payment", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create Pix payment"));
        }
    }
}