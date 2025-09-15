package br.hallel.relational.api.app.payment.checkout_transparent.controller;

import br.hallel.relational.api.app.payment.checkout_transparent.dto.MercadoPagoConfigDTO;
import br.hallel.relational.api.app.payment.checkout_transparent.service.ProcessPaymentNotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/payments/webhooks")
@Slf4j
@RequiredArgsConstructor
public class WebhookController {

    private final ProcessPaymentNotificationService service;
    private final ObjectMapper objectMapper;

    @PostMapping("/mercadopago")
    @Operation(summary = "Endpoint que vai escutar as notificações do Mercado Pago")
    public ResponseEntity<Void> handleMercadoPagoNotification(
            @RequestBody MercadoPagoConfigDTO inputData,
            @RequestHeader(value = "X-My-Test-Trigger", required = false) String testTrigger
            ) throws JsonProcessingException {

        // Log aprimorado para ver todos os campos recebidos
        log.info("Raw Mercado Pago Payload received: {}", objectMapper.writeValueAsString(inputData));

        if (testTrigger != null) {
            log.warn("!!! WEBHOOK CHAMADO POR UM GATILHO DE TESTE: {} !!!", testTrigger);
        }

        String resourceId = null;
        String notificationType = null;

        // Lógica para detectar o tipo de notificação (IPN ou Webhook antigo)
        if (inputData.getTopic() != null) {
            log.info("Processing IPN notification format.");
            notificationType = inputData.getTopic();
            // No formato IPN, o ID vem no campo 'resource', que na verdade é um Long dentro de 'data.id' de um pagamento
            // O campo 'resource' contém o ID do pagamento, que é o que precisamos
            resourceId = inputData.getResource();
        } else if (inputData.getType() != null && inputData.getData() != null && inputData.getData().getId() != null) {
            log.info("Processing Webhook notification format.");
            notificationType = inputData.getType();
            resourceId = inputData.getData().getId();
        }

        if (resourceId == null || notificationType == null) {
            log.warn("Could not determine notification type or resource ID from payload.");
            return ResponseEntity.ok().build();
        }

        if (!"payment".equalsIgnoreCase(notificationType)) {
            log.info("Ignoring non-payment notification type: {}", notificationType);
            return ResponseEntity.ok().build();
        }

        try {
            var result = service.processNotification(Long.parseLong(resourceId));
            log.info("Successfully processed payment notification: {} {}", result.isSuccess(), result.getStatus());
        } catch (Exception ex) {
            log.error("Error processing payment notification {}: {}", inputData, ex.getMessage(), ex);
        }

        return ResponseEntity.ok().build();
    }
}

