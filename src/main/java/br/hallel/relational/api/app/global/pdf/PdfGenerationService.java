package br.hallel.relational.api.app.global.pdf;

import br.hallel.relational.api.app.event.dto.EventScaleReportPDF;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.model.enum_type.TransactionType;
import br.hallel.relational.api.app.global.exception.GenerateComandaException;
import br.hallel.relational.api.app.global.pdf.dto.EventParticipationForPDF;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.PixPaymentData;
import br.hallel.relational.api.app.user.model.User;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import com.mercadopago.resources.payment.Payment;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
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
        List<EventParticipationForPDF> eventParticipationForPDFS = EventParticipationForPDF.fromListParticipation(
                eventParticipations);
        context.setVariable("participants", eventParticipationForPDFS);
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

    public ByteArrayOutputStream generatePixPaymentPdf(PixPaymentData pixData) throws IOException {

        Context context = new Context();
        context.setVariable("paymentId", pixData.paymentId());
        context.setVariable("eventTitle", pixData.eventTitle());
        context.setVariable("amount", pixData.amount());
        context.setVariable("pixCode", pixData.pixCode());
        String base64Image = pixData.qrCodeImageUrl();
        String dataUri = "data:image/png;base64," + base64Image;
        context.setVariable("qrCodeImageBase64", dataUri);
        context.setVariable("expirationDateTime", pixData.expirationDateTime());

        String html = templateEngine.process("pix_enrollment_payment", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();

        try {
            builder.useFont(new File("src/main/resources/static/fonts/Poppins-Regular.ttf"), "Poppins");
        } catch (Exception e) {
            log.warn("Fonte Poppins não encontrada, usando fonte padrão do sistema para o PDF Pix.", e);
        }

        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);
        builder.run();

        return outputStream;
    }

    public byte[] generateReportEventScale(EventScaleReportPDF dto, LocalDateTime start, LocalDateTime end) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font textoNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font cinza = new Font(Font.HELVETICA, 11, Font.NORMAL, new Color(90, 90, 90));

            Paragraph titulo = new Paragraph("Relatório " + dto.title() + " do Ministério: " + dto.ministry_name(), tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(12);
            document.add(titulo);

            Paragraph intervalo = new Paragraph("Intervalo: " + start.toLocalDate() + " até " + end.toLocalDate(), textoNormal);
            intervalo.setAlignment(Element.ALIGN_CENTER);
            intervalo.setSpacingAfter(20);
            document.add(intervalo);

            document.add(new Paragraph("📆 Eventos", subtituloFont));

            if (dto.events_title().isEmpty()) {
                document.add(new Paragraph("Nenhum evento encontrado.", textoNormal));
            } else {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH'h'mm'min'");
                ZoneId zonaBrasil = ZoneId.of("America/Sao_Paulo");

                for (int i = 0; i < dto.events_title().size(); i++) {
                    String title = dto.events_title().get(i);
                    String dataStrOriginal = dto.date_events().size() > i ? dto.date_events().get(i) : null;
                    String dataFormatada;
                    if (dataStrOriginal != null) {
                        try {
                            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dataStrOriginal, inputFormatter);
                            ZonedDateTime dataBrasileira = offsetDateTime.atZoneSameInstant(zonaBrasil);
                            dataFormatada = dataBrasileira.format(outputFormatter);
                        } catch (DateTimeParseException e) {
                            dataFormatada = "Data inválida";
                        }
                    } else {
                        dataFormatada = "Data inválida";
                    }
                    document.add(new Paragraph("• " + title + " - " + dataFormatada, textoNormal));
                }
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("👥 Coordenadores", subtituloFont));
            document.add(new Paragraph("- Coordenador: " + dto.coordinator_name(), textoNormal));
            document.add(new Paragraph("- Vice: " + dto.vice_coodinator_name(), textoNormal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("✅ Participantes (" + dto.participants().size() + ")", subtituloFont));
            if (dto.participants().isEmpty()) {
                document.add(new Paragraph("Nenhum participante.", textoNormal));
            } else {
                for (String p : new HashSet<>(dto.participants())) {
                    document.add(new Paragraph("• " + p, textoNormal));
                }
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("🎫 Convidados (" + dto.invited().size() + ")", subtituloFont));
            if (dto.invited().isEmpty()) {
                document.add(new Paragraph("Nenhum convidado.", textoNormal));
            } else {
                for (String c : new HashSet<>(dto.invited())) {
                    document.add(new Paragraph("• " + c, textoNormal));
                }
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("❌ Recusaram (" + dto.decline().size() + ")", subtituloFont));
            if (dto.decline().isEmpty()) {
                document.add(new Paragraph("Nenhuma recusa.", textoNormal));
            } else {
                for (String r : new HashSet<>(dto.decline())) {
                    document.add(new Paragraph("• " + r, textoNormal));
                }
            }

            document.add(new Paragraph("\n\nGeração: " + LocalDateTime.now(), cinza));
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}
