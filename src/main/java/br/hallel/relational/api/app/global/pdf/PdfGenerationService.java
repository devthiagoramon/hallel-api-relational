package br.hallel.relational.api.app.global.pdf;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.user.model.User;
import com.mercadopago.resources.payment.Payment;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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

    public byte[] generatePdfFromParticipationsInevent(List<EventParticipation> eventParticipations, Event event) throws IOException {
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

}
