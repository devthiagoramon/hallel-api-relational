package br.hallel.relational.api.app.email.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailUtils {
    private final JavaMailSender mailSender;
    @Getter
    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void sendMail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            helper.setText(text, true);

            mailSender.send(message);
            log.info("Remetente: {}",from);
            log.info("Email enviado com para {} com sucesso!",to);
        } catch (MessagingException e) {
            System.out.println("Erro ao enviar email HTML: " + e.getMessage());
        }
    }

}
