package br.hallel.relational.api.app.payment.controller;

import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.payment.dto.PixChargeRequest;
import br.hallel.relational.api.app.payment.service.PixService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/payment/pix")
public class PixController {
    @Autowired
    private PixService pixService;
    @Autowired
    private EventTransactionRepository eventTransactionRepository;

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    @PostMapping("/create-key")
    public ResponseEntity createPixEVP() {
        String webhookUrl = "https://hallel-api.onrender.com/payment/pix/configuration";
        JSONObject jsonObject = this.pixService.pixCreateEVP();
        String chaveCriada = jsonObject.getString("chave");
        this.pixService.pixConfigurarWebhook(chaveCriada, webhookUrl);


        return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());
    }


    @PostMapping("/create/charge")
    public ResponseEntity createPixCharge(@RequestBody PixChargeRequest dto) {
        JSONObject jsonObject = this.pixService.pixGenerateQrCode(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());
    }


    @GetMapping("/status/{txid}")
    public ResponseEntity<String> checkPixStatus(@PathVariable String txid) {
        JSONObject response = this.pixService.pixDetailCharge(txid);
        if (response != null) {
            return ResponseEntity.ok(response.toString());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pagamento não encontrado ou erro na consulta.");
    }

    @GetMapping("/list-all")
    public ResponseEntity listAllPayments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia
    ) {
        JSONObject response = pixService.listPixPayments(dia);
        if (response != null) {
            return ResponseEntity.ok(response.toString());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao listar pagamentos do dia");
    }

    @PostMapping("/webhook/configure")
    public ResponseEntity<String> configureWebhook(@RequestParam String chavePix) {
        String webhookUrl = "https://hallel-api.onrender.com/payment/pix/configuration";
        JSONObject response = pixService.pixConfigurarWebhook(chavePix, webhookUrl);

        if (response != null) {
            return ResponseEntity.ok("Webhook configurado com sucesso.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao configurar o webhook.");
    }

    @PostMapping("/configuration")
    public ResponseEntity<String> receivePixWebhook(@RequestBody String payload) {
        System.out.println("Webhook Pix recebido. Conteúdo: " + payload);

        try {
            JSONObject json = new JSONObject(payload);

            // A documentação da Efí mostra que o evento vem como uma lista
            if (json.has("eventos")) {
                for (Object eventObject : json.getJSONArray("eventos")) {
                    JSONObject event = (JSONObject) eventObject;

                    // Aqui você deve verificar o nome do evento, que é "pix.received"
                    if ("pix.received".equals(event.getString("nome"))) {
                        // O objeto "pix" está dentro do objeto "pix" que está no evento
                        JSONObject pix = event.getJSONObject("pix");
                        String txid = pix.getString("txid");

                        Optional<EventParticipation> optionalParticipation = eventParticipationRepository.findByPixTxid(txid);
                        if (optionalParticipation.isPresent()) {
                            EventParticipation participation = optionalParticipation.get();

                            // O status de pagamento já é confirmado pela própria notificação.
                            participation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);
                            participation.setHasParticipated(true);
                            eventParticipationRepository.save(participation);
                            System.out.println("Participação do evento atualizada para PAGO. Txid: " + txid);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            System.err.println("Erro ao processar o JSON do webhook: " + e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao processar JSON.");
        } catch (Exception e) {
            System.err.println("Erro inesperado no webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno no servidor.");
        }

        return ResponseEntity.ok("Webhook recebido com sucesso.");
    }
}