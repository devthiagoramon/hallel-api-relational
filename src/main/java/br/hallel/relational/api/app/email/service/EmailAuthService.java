package br.hallel.relational.api.app.email.service;

import br.hallel.relational.api.app.email.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAuthService {
    private final TemplateEngine templateEngine;
    private final EmailUtils emailUtils;

    @Async("emailTaskExecutor")
    public void sendAdminMail(String to, String email, String url) {
        try {

            Context context = new Context();
            context.setVariable("url_validate", url);
            context.setVariable("email", email);

            String html = templateEngine.process("send_email_admin", context);
            emailUtils.sendMail(to, "Validar administrador", html);
        } catch (Exception e) {
            log.info("Erro ao enviar email de validação do administrador HTML: " + e.getMessage());
        }
    }

    @Async
    public void sendSignUpMail(String to, String name) {
        try {

            Context context = new Context();
            context.setVariable("name", name);
            String html = templateEngine.process("send_email_signup", context);
            emailUtils.sendMail(to, "Confirmação de cadastro", html);
        } catch (Exception e) {
            log.info("Erro ao enviar email de confirmação de cadastro HTML: " + e.getMessage());
        }
    }
}
