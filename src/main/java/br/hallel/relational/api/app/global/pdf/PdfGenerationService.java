package br.hallel.relational.api.app.global.pdf;

import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.global.exception.GenerateComandaException;
import br.hallel.relational.api.app.user.model.User;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.mercadopago.resources.payment.Payment;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Slf4j @Service
public class PdfGenerationService {
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Gera um PDF a partir de um objeto de Pagamento do Mercado Pago.
     *
     * @param paymentDetails O objeto contendo os detalhes da transação.
     * @return Um array de bytes (byte[]) representando o arquivo PDF.
     */
    public byte[] generatePdfFromPayment(Payment paymentDetails, User user) throws IOException {

        Context context = new Context();
        context.setVariable("payment", paymentDetails);
        context.setVariable("user", user);


        String html = templateEngine.process("comprovante_pagamento", context);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, null); // O segundo argumento é a base URI, null é ok para este caso
        builder.toStream(outputStream);
        builder.run();


        return outputStream.toByteArray();
    }

    public byte[] generatePdfFromParticipationsInevent(List<EventParticipation> eventParticipations, Event event) throws
            IOException {
        Context context = new Context();
        context.setVariable("participants", eventParticipations);
        context.setVariable("event", event);
        String html = templateEngine.process("participantes_evento", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);
        builder.run();

        return outputStream.toByteArray();
    }

    public String gerarComandaAlimentoBase64(FoodTransaction foodTransaction) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (EscPos escPos = new EscPos(buffer)) {
            Style titleStyle = new Style()
                    .setJustification(EscPosConst.Justification.Center)
                    .setBold(true)
                    .setFontSize(Style.FontSize._2, Style.FontSize._2); // Tamanho duplo

            Style normalCenter = new Style().setJustification(EscPosConst.Justification.Center);
            Style totalStyle = new Style().setBold(true);

            escPos.write(titleStyle, "Comunidade Hallel");
            escPos.feed(1);

            escPos.write(normalCenter, "Comanda: " + foodTransaction.getId().toString() + "\n");
            escPos.write(normalCenter, "----------------------------------------\n");
            escPos.feed(1);

            escPos.write("Qtd  Produto              Preco     Total\n");
            for (FoodSaleItem item : foodTransaction.getSaleItems()) {
                double totalItem = item.getQuantity() * item.getFood().getValue();
                String linha = String.format("%-4d %-20.20s %-9.2f %-7.2f",
                        item.getQuantity(),
                        item.getFood().getName(),
                        item.getFood().getValue(),
                        totalItem);
                escPos.write(linha + "\n");
            }
            escPos.write(normalCenter, "----------------------------------------\n");
            String totalLine = String.format("Total: R$ %.2f", foodTransaction.getValue());
            escPos.write(totalStyle, totalLine + "\n");
            escPos.feed(2);

            escPos.cut(EscPos.CutMode.FULL);
        } catch (IOException e) {
            // Trate a exceção conforme necessário (ex: log)
            throw new GenerateComandaException("Não possivel gerar a comanda dos alimentos");
        }

        return Base64.getEncoder().encodeToString(buffer.toByteArray());
    }

    public byte[] generatePDFTransactionsEvents(Event event, List<EventTransaction> eventTransactions,
                                                TransactionType filter, Double balance, Double incoming,
                                                Double outputs) throws IOException {
        Context context = new Context();
        context.setVariable("transactions", eventTransactions);
        context.setVariable("event", event);
        context.setVariable("filter", filter);
        context.setVariable("balance", balance);
        context.setVariable("incoming", incoming);
        context.setVariable("outputs", outputs);


        try {
            // Carrega a imagem da pasta resources/static/images
            ClassPathResource imageResource = new ClassPathResource("static/images/logo-hallel.png");
            byte[] imageBytes = StreamUtils.copyToByteArray(imageResource.getInputStream());
            String imageAsBase64 = Base64.getEncoder().encodeToString(imageBytes);

            // 3. Adicione a string Base64 ao contexto
            context.setVariable("logoHallelBase64", imageAsBase64);

        } catch (IOException e) {
            // Lide com o erro, talvez logando ou usando uma imagem padrão
            log.error("e: ", e);
        }


        String html = templateEngine.process("eventos_transacoes", context);
        html = html.replace("\uFEFF", "");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFont(new File("src/main/resources/static/fonts/Poppins-Regular.ttf"), "Poppins");
        builder.useFastMode();
        builder.withHtmlContent(html, null); // O segundo argumento é a base URI, null é ok para este caso
        builder.toStream(outputStream);
        builder.run();


        return outputStream.toByteArray();
    }

}
