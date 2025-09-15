package br.hallel.relational.api.app.event.controller;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public/test")
@RequiredArgsConstructor
public class TesteController {

    private static final Logger log = LoggerFactory.getLogger(TesteController.class);
    private final SimpMessagingTemplate template;


    @GetMapping("/notify/{transactionId}")
    public String testNotify(@PathVariable String transactionId) {
        log.info("--- DISPARANDO NOTIFICAÇÃO DE TESTE PARA A TRANSAÇÃO: {} ---", transactionId);

        Map<String, String> payload = new HashMap<>();
        payload.put("statusPaymentEventParticipation", "PAGO");

        try {
            template.convertAndSend("/topic/payments/" + transactionId, payload);
            log.info("--- NOTIFICAÇÃO DE TESTE ENVIADA COM SUCESSO. ---");
            return "Notificação de teste enviada para " + transactionId;
        } catch (Exception e) {
            log.error("--- FALHA AO ENVIAR NOTIFICAÇÃO DE TESTE: {} ---", e.getMessage(), e);
            return "Falha ao enviar notificação: " + e.getMessage();
        }
    }
}