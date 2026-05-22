package br.hallel.relational.api.app.email.service;

import br.hallel.relational.api.app.email.dto.EmailParticipationDTO;
import br.hallel.relational.api.app.email.utils.EmailUtils;
import br.hallel.relational.api.app.global.pdf.PdfGenerationService;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreatePixPaymentRequestDTO;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.PixPaymentData;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import br.hallel.relational.api.app.user.service.UserService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context; // Importação essencial para o Thymeleaf

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailEventParticipationService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TemplateEngine templateEngine;
    private final EmailUtils emailUtils;

    // Formato padrão para o Brasil
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private final PdfGenerationService pdfGenerationService;

    // 1. E-mail de Aniversário
    @Async
    public boolean sendBirthDayEmail(String to, String subject, String name, User user, LocalDate today) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", name);
            // O template Thymeleaf deve estar em 'src/main/resources/templates/birthday.html'
            String html = templateEngine.process("birthday", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Email de aniversário enviado com sucesso para: {}", to);

            user.setBirthdayEmailSentIn(today);
            userRepository.save(user);
            this.userService.sendNotificationBirthDayMessage(user);
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar email de aniversário para {}: {}", to, e.getMessage());
            return false;
        }
    }

    // 2. E-mail de Cobrança (Associação Suspensa)
    @Async
    public boolean sendBillingAssociate(String to, String subject, String name, Double value,
                                        LocalDateTime renewalDate) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", name);
            context.setVariable("value", String.format(LOCALE_PT_BR, "%.2f", value)); // Formatação para R$
            context.setVariable("renewalDate", renewalDate.format(DATE_FORMATTER));
            // O template Thymeleaf deve estar em 'src/main/resources/templates/billing-associate.html'
            String html = templateEngine.process("billing-associate", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Email de cobrança (associação suspensa) enviado com sucesso para: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar email de cobrança (associação suspensa) para {}: {}", to, e.getMessage());
            return false;
        }
    }


    // 3. E-mail de Renovação
    @Async
    public boolean sendRenewalEmail(String to, String name, Double value, LocalDateTime renewalDate) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", name);
            context.setVariable("value", String.format(LOCALE_PT_BR, "%.2f", value)); // Formatação para R$
            context.setVariable("renewalDate", renewalDate.format(DATE_FORMATTER));
            String html = templateEngine.process("renewal", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(to);
            helper.setSubject("Lembrete: Sua Associação Hallel Vence em Breve!");
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Email de renovação enviado para: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar email de renovação para {}: {}", to, e.getMessage());
            return false;
        }
    }

    // 4. E-mail de Lembrete de Participação em Evento
    @Async
    public void sendEventParticipationReminderEmail(
            EmailParticipationDTO dto, // REFATORADO
            String eventId,
            boolean isMorningReminder,
            boolean isPaid
    ) {
        try {
            String subject = isMorningReminder
                    ? "Lembrete: O evento " + dto.eventTitle() + " é hoje! 🎉"
                    : "Falta 1 hora para o evento " + dto.eventTitle() + " começar ⏰";

            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", dto.name());
            context.setVariable("eventTitle", dto.eventTitle());
            context.setVariable("eventDate", dto.eventDate().format(DATETIME_FORMATTER));
            context.setVariable("eventId", eventId);
            context.setVariable("isPaid", isPaid);
            context.setVariable("isMorningReminder", isMorningReminder);
            // O template Thymeleaf deve estar em 'src/main/resources/templates/event-reminder.html'
            String html = templateEngine.process("event-reminder", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(dto.to());
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Lembrete de evento enviado para: {}", dto.to());

        } catch (Exception e) {
            log.error("Erro ao enviar lembrete de evento para {}: {}", dto.to(), e.getMessage());
            throw new IllegalStateException("Falha ao enviar e-mail de lembrete", e);
        }
    }

    @Async
    public void sendPaymentJoinEvent(
            EmailParticipationDTO dto,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String eventId,
            PixPaymentData pixData
    ) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", dto.name());
            context.setVariable("eventTitle", dto.eventTitle());

            // Formata as datas de início e fim para mostrar o período completo
            // Usamos eventDate do DTO como startTime
            context.setVariable("startTime", startTime.format(DATETIME_FORMATTER));
            if (endTime != null) {

                context.setVariable("endTime", endTime.format(DATETIME_FORMATTER));
            }

            context.setVariable("eventId", eventId);

            // Adiciona dados do Pix para exibir no corpo do e-mail
            context.setVariable("paymentValue", pixData.amount());

            String html = templateEngine.process("event-enrollment-payment", context);

            MimeMessage message = mailSender.createMimeMessage();

            // O helper agora PRECISA de 'true' para permitir anexos
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(dto.to());
            helper.setSubject("🎉 Confirmação de Inscrição e Pagamento: " + dto.eventTitle());
            helper.setText(html, true);

            ByteArrayOutputStream pdfOutputStream = pdfGenerationService.generatePixPaymentPdf(pixData);

            helper.addAttachment("Pix_Pagamento_" + eventId + ".pdf",
                    new ByteArrayResource(pdfOutputStream.toByteArray()),
                    "application/pdf");

            mailSender.send(message);
            log.info("Confirmação de inscrição e pagamento enviada para: {}", dto.to());

        } catch (Exception e) {
            log.error("Erro ao processar e-mail assíncrono: {}", e.getMessage(), e);
        }
    }

    // 5. E-mail de Comprovante de Participação em Evento
    @Async
    public void sendComprovantEventParticipation(
            EmailParticipationDTO dto, // REFATORADO
            String eventId,
            String whatsAppGroupLink
    ) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", dto.name());
            context.setVariable("eventTitle", dto.eventTitle());
            context.setVariable("eventDate", dto.eventDate().format(DATETIME_FORMATTER));
            context.setVariable("eventId", eventId);
            context.setVariable("whatsAppGroupLink", whatsAppGroupLink);

            String html = templateEngine.process("event-comprovant", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(dto.to());
            helper.setSubject("Comprovante de Participação no Evento: " + dto.eventTitle());
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Comprovante de participação enviado para: {}", dto.to());

        } catch (Exception e) {
            log.error("Erro ao enviar comprovante para {}: {}", dto.to(), e.getMessage());
            throw new IllegalStateException("Falha ao enviar email de confirmação", e);
        }
    }

    // 6. E-mail de Reembolso/Saída de Evento
    @Async
    public void sendRefundEventParticipation(
            EmailParticipationDTO dto, // REFATORADO
            Double amountRefunded
    ) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", dto.name());
            context.setVariable("eventTitle", dto.eventTitle());
            context.setVariable("eventDate", dto.eventDate().format(DATETIME_FORMATTER));
            context.setVariable("amountRefunded", String.format(LOCALE_PT_BR, "%.2f", amountRefunded));
            // O template Thymeleaf deve estar em 'src/main/resources/templates/event-refund.html'
            String html = templateEngine.process("event-refund", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(dto.to());
            helper.setSubject("Confirmação de saída do Evento: " + dto.eventTitle());
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Email de saída do evento enviado para: {}", dto.to());

        } catch (Exception e) {
            log.error("Erro ao enviar email de saída do evento para {}: {}", dto.to(), e.getMessage());
            throw new IllegalStateException("Falha ao enviar email de saída do evento", e);
        }
    }

    @Async
    public void sendNotificationEventQueue(
            EmailParticipationDTO dto,
            int positionInQueue,
            String eventId
    ) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", dto.name());
            context.setVariable("eventTitle", dto.eventTitle());
            context.setVariable("eventDate", dto.eventDate().format(DATETIME_FORMATTER));
            context.setVariable("positionInQueue", positionInQueue);
            context.setVariable("confirmationLink", "http://localhost:5173/evento/" + eventId);
            String html = templateEngine.process("event-queue-notification", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(dto.to());
            helper.setSubject("Você está na Fila de Espera: " + dto.eventTitle());
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Email de notificação de fila enviado para: {}", dto.to());

        } catch (Exception e) {
            log.error("Erro ao enviar notificação de fila para {}: {}", dto.to(), e.getMessage());
            throw new IllegalStateException("Falha ao enviar e-mail de notificação de fila", e);
        }
    }

    @Async
    public void sendQueueSpaceAvailableNotification(
            EmailParticipationDTO dto,
            String autoLoginUrl  // ← Mude o parâmetro para receber a URL
    ) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", dto.name());
            context.setVariable("eventTitle", dto.eventTitle());
            context.setVariable("eventDate", dto.eventDate().format(DATETIME_FORMATTER));
            context.setVariable("confirmationLink", autoLoginUrl);  // ← Usa a URL recebida

            String html = templateEngine.process("confirm-queue", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(dto.to());
            helper.setSubject("🎉 Vaga Liberada! Confirme Sua Inscrição em " + dto.eventTitle());
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Email de notificação de vaga liberada enviado para: {}", dto.to());
            log.info("Link de confirmação: {}", autoLoginUrl);

        } catch (Exception e) {
            log.error("Erro ao enviar notificação de vaga liberada para {}: {}", dto.to(), e.getMessage());
            throw new IllegalStateException("Falha ao enviar e-mail de notificação de vaga liberada", e);
        }
    }

    // 9. E-mail de Ingresso para Evento Gratuito
    @Async
    public void sendFreeEventTicket(
            EmailParticipationDTO dto,
            String participationId,
            String localEventName,
            String eventId,
            String whatsAppGroupLink
    ) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", dto.name());
            context.setVariable("eventTitle", dto.eventTitle());
            context.setVariable("eventDate", dto.eventDate().format(DATETIME_FORMATTER));
            context.setVariable("localEventName", localEventName);
            context.setVariable("ticketNumber", participationId.substring(0, 8).toUpperCase());
            context.setVariable("eventId", eventId);
            context.setVariable("whatsAppGroupLink", whatsAppGroupLink);

            String html = templateEngine.process("event-free-ticket", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(dto.to());
            helper.setSubject("Seu ingresso para: " + dto.eventTitle());
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Ingresso gratuito enviado para: {}", dto.to());

        } catch (Exception e) {
            log.error("Erro ao enviar ingresso gratuito para {}: {}", dto.to(), e.getMessage());
            throw new IllegalStateException("Falha ao enviar e-mail de ingresso gratuito", e);
        }
    }
}