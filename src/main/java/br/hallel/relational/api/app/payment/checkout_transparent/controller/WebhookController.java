package br.hallel.relational.api.app.payment.checkout_transparent.controller;

import br.hallel.relational.api.app.payment.checkout_transparent.dto.MercadoPagoConfigDTO;
import br.hallel.relational.api.app.payment.checkout_transparent.service.ProcessPaymentNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/payments/webhooks")
@Slf4j
@RequiredArgsConstructor
public class WebhookController {

    private final ProcessPaymentNotificationService service;

    @PostMapping("/mercadopago")
    @Operation(summary = "endpoint que vai escutar as notificações do mercado pago")
    public ResponseEntity<Void> handleMercadoPagoNotification(
            @RequestBody(required = false) MercadoPagoConfigDTO inputData) {

        if (inputData == null || inputData.getData() == null || inputData.getData().getId() == null || inputData.getType() == null) {
            log.warn("Received invalid Mercado Pago notification: {}", inputData);
            return ResponseEntity.ok().build(); // Always acknowledge
        }

        String resourceId = inputData.getData().getId(); // payment id
        String resourceType = inputData.getType(); // e.g., "payment"

        if (!"payment".equalsIgnoreCase(resourceType)) {
            log.info("Ignoring non-payment notification type: {}", resourceType);
            return ResponseEntity.ok().build(); // Acknowledge but skip processing
        }

        try {

            var result = service.processNotification(Long.parseLong(resourceId));
            log.info("Successfully processed payment notification: {} {}", result.isSuccess(), result.getStatus());
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error processing payment notification {}: {}", inputData, ex.getMessage(), ex);
        }

        return ResponseEntity.ok().build();
    }
}

