package br.hallel.relational.api.app.payment.controller;

import br.hallel.relational.api.app.payment.dto.PixChargeRequest;
import br.hallel.relational.api.app.payment.dto.PixWebhookPayloadDTO;
import br.hallel.relational.api.app.payment.service.PixService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/payment/pix")
public class PixController {
    @Autowired
    private PixService pixService;

    @GetMapping("/configuration")
    public String configurarWebhookPix() {
        String chavePix = "5c044047-176f-4a97-b331-0eb6d9aba6da";
        String webhookUrl = "https://hallel-api.onrender.com/payment/pix/configuration";

        try {
            pixService.pixConfigurarWebhook(chavePix, webhookUrl);
            return "Webhook configurado com sucesso!";
        } catch (Exception e) {
            return "Erro ao configurar o webhook: " + e.getMessage();
        }
    }


    @PostMapping("/create-key")
    public ResponseEntity createPixEVP() {
        JSONObject jsonObject = this.pixService.pixCreateEVP();
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());
    }


    @PostMapping("/create/charge")
    public ResponseEntity createPixCharge(@RequestBody PixChargeRequest dto) {
        JSONObject jsonObject = this.pixService.pixGenerateQrCode(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonObject.toString());
    }

    public ResponseEntity<Void> receivePixWebhook(@RequestBody PixWebhookPayloadDTO payload) {

        System.out.println("Recebi webhook PIX: " + payload);
        pixService.processWebhookNotification(payload);
        return ResponseEntity.ok().build();
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

}
