package br.hallel.relational.api.app.payment.service;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import br.hallel.relational.api.app.event.model.EventTransaction;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.TransactionType;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.payment.dto.PixChargeRequest;
import br.hallel.relational.api.app.payment.dto.PixWebhookPayloadDTO;
import br.hallel.relational.api.app.payment.model.Credentials;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service
public class PixService {
//    @Value("${CLIENT_ID}")
//    private String clientId;
//
//    @Value("${CLIENT_SECRET}")
//    private String clientSecret;

    @Autowired
    private EventParticipationRepository eventParticipationRepository;
    @Autowired
    private EventTransactionRepository eventTransactionRepository;
    @Autowired
    private Credentials credentials;

    public JSONObject pixCreateEVP() {
        JSONObject options = configuringJsonObject();
        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixCreateEvp", new HashMap<String, String>(), new JSONObject());
            System.out.println(response);
            return response;
        } catch (EfiPayException e) {
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e);
        }
        return null;
    }


    public JSONObject pixGenerateQrCode(PixChargeRequest dto) {

        JSONObject options = configuringJsonObject();

        JSONObject body = new JSONObject();
        body.put("calendario", new JSONObject().put("expiracao", 3600));
        body.put("devedor", new JSONObject().put("cpf", "12345678909").put("nome", "Miguel Arcanjo"));
        body.put("valor", new JSONObject().put("original", dto.valor()));
        body.put("chave", dto.chave());

        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixCreateImmediateCharge", new HashMap<String, String>(), body);
            System.out.println(response);
            return response;
        } catch (EfiPayException e) {
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public JSONObject pixCreateCharge(String chave, String valor) {
        JSONObject options = configuringJsonObject();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("txid", "7978c0c97ea847e78e8849634473c1f1");

        JSONObject body = new JSONObject();

        body.put("solicitacaoPagador", "Serviço realizado.");

        JSONArray infoAdicionais = new JSONArray();
        infoAdicionais.put(new JSONObject().put("nome", "Campo 1").put("valor", "Informação Adicional1 do PSP-Recebedor"));
        infoAdicionais.put(new JSONObject().put("nome", "Campo 2").put("valor", "Informação Adicional2 do PSP-Recebedor"));
        body.put("infoAdicionais", infoAdicionais);

        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixCreateCharge", params, body);
            int idFromJso = response.getJSONObject("loc").getInt("id");

            System.out.println(response);
            return response;
        } catch (EfiPayException e) {
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private JSONObject configuringJsonObject() {
        JSONObject options = new JSONObject();
        options.put("client_id", this.credentials.getClientId());
        options.put("client_secret", this.credentials.getClientSecret());

        try {
            // Salva o conteúdo em um arquivo temporário e obtém o caminho
            String certificatePath = createTempCertificateFile(this.credentials.getCertificateContent());
            options.put("certificate", certificatePath);
        } catch (IOException e) {
            // Trata o erro de criação de arquivo
            System.err.println("Erro ao criar arquivo de certificado temporário: " + e.getMessage());
            return null; // Retorna nulo para indicar falha
        }

        options.put("sandbox", this.credentials.isSandbox());
        return options;
    }

    private String createTempCertificateFile(String content) throws IOException {
        // Cria um arquivo temporário com o sufixo .p12
        File tempFile = File.createTempFile("hallel-certificado", ".p12");

        // Garante que o arquivo seja deletado quando a aplicação for fechada
        tempFile.deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            // CORREÇÃO: Decodifica a string Base64 para bytes binários
            byte[] decodedBytes = Base64.getDecoder().decode(content);
            fos.write(decodedBytes);
        }

        return tempFile.getAbsolutePath();
    }

    public void processWebhookNotification(PixWebhookPayloadDTO payload) {
        if (payload.getPix() != null && !payload.getPix().isEmpty()) {

            payload.getPix().forEach(p -> {
                System.out.printf(
                        "Pagamento confirmado: TXID=%s, E2E=%s, Valor=%s, Horário=%s%n",
                        p.getTxid(),
                        p.getEndToEndId(),
                        p.getValor(),
                        p.getHorario()
                );

                // 1. Busque a participação pelo TXID
                eventParticipationRepository.findByPixTxid(p.getTxid()).ifPresent(participation -> {
                    // 2. Atualize o status para PAGO
                    participation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);

                    // 3. Salve a transação
                    EventTransaction transaction = new EventTransaction();
                    transaction.setEvent(participation.getEvent());
                    transaction.setValue(Double.valueOf(p.getValor()));
                    transaction.setDateTransaction(new Date());
                    transaction.setTransactionType(TransactionType.ENTRADA);
                    transaction.setDesciption("Pagamento do Participante " +
                            participation.getUser().getName() + " para o Evento " + participation.getEvent().getTitle());

                    eventParticipationRepository.save(participation);
                    eventTransactionRepository.save(transaction);
                });
            });
        }
    }

    public JSONObject pixDetailCharge(String txid) {
        JSONObject options = configuringJsonObject();
        HashMap<String, String> params = new HashMap<>();
        params.put("txid", txid);

        try {
            EfiPay efi = new EfiPay(options);
            return efi.call("pixDetailCharge", params, new JSONObject());
        } catch (EfiPayException e) {
            System.out.println("Erro EFÍ: " + e.getError() + " - " + e.getErrorDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject listPixPayments(LocalDate dia) {
        // Define início e fim do dia em UTC

        ZonedDateTime inicioUTC = dia.atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime fimUTC = dia.atTime(LocalTime.MAX).atZone(ZoneId.of("UTC"));

        String inicioISO = inicioUTC.format(DateTimeFormatter.ISO_INSTANT); // 2025-08-14T00:00:00Z
        String fimISO = fimUTC.format(DateTimeFormatter.ISO_INSTANT);

        JSONObject options = configuringJsonObject();
        HashMap<String, String> params = new HashMap<>();
        params.put("inicio", inicioISO);
        params.put("fim", fimISO);

        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixReceivedList", params, new JSONObject());
            System.out.println(response.toString());
            return response;
        } catch (EfiPayException e) {
            System.out.println("Erro EFÍ: " + e.getError() + " - " + e.getErrorDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject pixConfigurarWebhook(String chavePix, String webhookUrl) {

        JSONObject options = configuringJsonObject();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("chave", chavePix);

        JSONObject body = new JSONObject();
        body.put("webhookUrl", webhookUrl);

        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixConfigWebhook", params, body);

            System.out.println("Webhook configurado com sucesso: " + response);
            return response;

        } catch (EfiPayException e) {
            System.err.println("Erro EFÍ: " + e.getError() + " - " + e.getErrorDescription());
            return null;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }
}
