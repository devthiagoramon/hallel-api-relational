package br.hallel.relational.api.app.email.service;

import br.hallel.relational.api.app.email.utils.EmailUtils;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import br.hallel.relational.api.app.user.service.UserService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context; // Importação essencial para o Thymeleaf

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
    public boolean sendBillingAssociate(String to, String subject, String name, Double value, LocalDateTime renewalDate) {
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
            // O template Thymeleaf deve estar em 'src/main/resources/templates/renewal.html'
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
            String to,
            String name,
            LocalDateTime eventDate,
            String eventTitle,
            String eventId,
            boolean isMorningReminder,
            boolean isPaid
    ) {
        try {
            String subject = isMorningReminder
                    ? "Lembrete: O evento " + eventTitle + " é hoje! 🎉"
                    : "Falta 1 hora para o evento " + eventTitle + " começar ⏰";

            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", name);
            context.setVariable("eventTitle", eventTitle);
            context.setVariable("eventDate", eventDate.format(DATETIME_FORMATTER));
            context.setVariable("eventId", eventId);
            context.setVariable("isPaid", isPaid);
            context.setVariable("isMorningReminder", isMorningReminder);
            // O template Thymeleaf deve estar em 'src/main/resources/templates/event-reminder.html'
            String html = templateEngine.process("event-reminder", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Lembrete de evento enviado para: {}", to);

        } catch (Exception e) {
            log.error("Erro ao enviar lembrete de evento para {}: {}", to, e.getMessage());
            throw new IllegalStateException("Falha ao enviar e-mail de lembrete", e);
        }
    }


    // 5. E-mail de Comprovante de Participação em Evento
    @Async
    public void sendComprovantEventParticipation(String to, String name,
                                                 LocalDateTime eventDate,
                                                 String eventTitle,
                                                 String eventId,
                                                 String whatsAppGroupLink) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", name);
            context.setVariable("eventTitle", eventTitle);
            context.setVariable("eventDate", eventDate.format(DATETIME_FORMATTER));
            context.setVariable("eventId", eventId);
            context.setVariable("whatsAppGroupLink", whatsAppGroupLink);
            // O template Thymeleaf deve estar em 'src/main/resources/templates/event-comprovant.html'
            String html = templateEngine.process("event-comprovant", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(to);
            helper.setSubject("Comprovante de Participação no Evento: " + eventTitle);
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Comprovante de participação enviado para: {}", to);

        } catch (Exception e) {
            log.error("Erro ao enviar comprovante para {}: {}", to, e.getMessage());
            throw new IllegalStateException("Falha ao enviar email de confirmação", e);
        }
    }

    // 6. E-mail de Reembolso/Saída de Evento
    @Async
    public void sendRefundEventParticipation(String to, String name,
                                             LocalDateTime eventDate,
                                             String eventTitle,
                                             Double amountRefunded) {
        try {
            Context context = new Context(LOCALE_PT_BR);
            context.setVariable("name", name);
            context.setVariable("eventTitle", eventTitle);
            context.setVariable("eventDate", eventDate.format(DATETIME_FORMATTER));
            context.setVariable("amountRefunded", String.format(LOCALE_PT_BR, "%.2f", amountRefunded));
            // O template Thymeleaf deve estar em 'src/main/resources/templates/event-refund.html'
            String html = templateEngine.process("event-refund", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailUtils.getFrom());
            helper.setTo(to);
            helper.setSubject("Confirmação de saída do Evento: " + eventTitle);
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Email de saída do evento enviado para: {}", to);

        } catch (Exception e) {
            log.error("Erro ao enviar email de saída do evento para {}: {}", to, e.getMessage());
            throw new IllegalStateException("Falha ao enviar email de saída do evento", e);
        }
    }
}