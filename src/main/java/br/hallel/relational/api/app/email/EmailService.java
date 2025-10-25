package br.hallel.relational.api.app.email;

import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import br.hallel.relational.api.app.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            helper.setText(text, true);

            mailSender.send(message);
            System.out.println("Email enviado com sucesso!");
        } catch (MessagingException e) {
            System.out.println("Erro ao enviar email HTML: " + e.getMessage());
        }
    }

    public void sendAdminMail(String to, String email, String url) {
        try {

            Context context = new Context();
            context.setVariable("url_validate", url);
            context.setVariable("email", email);

            String html = templateEngine.process("send_email_admin", context);
            sendMail(to, "Validar administrador", html);
        } catch (Exception e) {
            log.info("Erro ao enviar email de validação do administrador HTML: " + e.getMessage());
        }
    }

    public boolean sendBirthDayEmail(String to, String subject, String name, User user, LocalDate today) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            String html = """
                    <html>
                        <body style="font-family: Arial, sans-serif; color: #333;">
                            <div style="max-width: 600px; margin: auto; padding: 20px; background: #f9f9f9; border-radius: 10px;">
                                <h2 style="color: #4CAF50;">Feliz Aniversário, %s! 🎉</h2>
                                <p style="font-size: 16px;">
                                    Hoje é um dia muito especial e não poderíamos deixar de te desejar um feliz aniversário cheio de alegria, saúde e realizações!
                                </p>
                                <p style="font-size: 16px;">
                                    Que seu dia seja repleto de amor e carinho. Parabéns! 🎂
                                </p>
                                <p style="font-size: 16px; margin-top: 20px;">
                                    Que Deus continue a derramar Suas bênçãos sobre você, guiando seus passos e fortalecendo sua fé a cada novo dia.
                                </p>
                                <p style="font-size: 16px;">
                                    Que a luz de Cristo ilumine sua vida, enchendo seu coração de paz, esperança e amor.
                                </p>
                                <p style="font-size: 16px;">
                                    Estamos gratos por tê-lo em nossa comunidade e oramos para que você sinta a presença divina em todos os momentos.
                                </p>
                                <br/>
                                <p style="font-size: 14px; color: #888;">Enviado com carinho pela nossa equipe 💌</p>
                            </div>
                        </body>
                    </html>
                    """.formatted(name);

            helper.setText(html, true);
            mailSender.send(message);
            user.setBirthdayEmailSentIn(today);
            System.out.println("Email enviado com sucesso!");

            userRepository.save(user);
            this.userService.sendNotificationBirthDayMessage(user);
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao enviar email HTML: " + e.getMessage());
            return false;
        }
    }

    public boolean sendBillingAssociate(String to, String subject, String name, Double value, LocalDateTime renewalDate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Nota: Removi 'User user, LocalDate today' da assinatura,
            // pois este email não é para aniversário e não precisa atualizar o User.

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            // Formata a data de renovação
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedRenewalDate = renewalDate.format(formatter);

            String html = """
                    <html>
                        <body style="font-family: Arial, sans-serif; color: #333;">
                            <div style="max-width: 600px; margin: auto; padding: 20px; background: #fff5f5; border: 1px solid #ffcccc; border-radius: 10px;">
                                <h2 style="color: #D32F2F;">Associação Suspensa, %s</h2>
                                <p style="font-size: 16px;">
                                    Identificamos que a data de renovação da sua Associação Hallel expirou em <b>%s</b>.
                                </p>
                                <p style="font-size: 16px;">
                                    Devido ao atraso, seu status foi alterado para <b>SUSPENSO</b>. 
                                    Para reativar imediatamente seus benefícios, realize a renovação.
                                </p>
                    
                                <h3 style="color: #333;">Detalhes da Cobrança:</h3>
                                <ul style="list-style: none; padding: 0; font-size: 16px;">
                                    <li style="margin-bottom: 5px;"><strong>Valor da Mensalidade:</strong> R$ %.2f</li>
                                    <li style="margin-bottom: 5px;"><strong>Data de Vencimento:</strong> %s</li>
                                </ul>
                    
                                <div style="text-align: center; margin-top: 30px;">
                                    <a href="[LINK PARA PÁGINA DE RENOVAÇÃO]" 
                                       style="background-color: #D32F2F; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                                        Clique aqui para Renovar Agora
                                    </a>
                                </div>
                    
                                <p style="font-size: 14px; color: #888; margin-top: 30px;">
                                    Se você já realizou o pagamento, por favor, ignore esta mensagem. Caso contrário, reative sua associação para continuar a fazer parte da nossa comunidade.
                                </p>
                                <br/>
                                <p style="font-size: 14px; color: #888;">Equipe de Associação Hallel 🔔</p>
                            </div>
                        </body>
                    </html>
                    """.formatted(name, formattedRenewalDate, value, formattedRenewalDate);

            helper.setText(html, true);
            mailSender.send(message);

            System.out.println("Email de cobrança enviado com sucesso!");
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao enviar email de cobrança: " + e.getMessage());
            return false;
        }
    }


    public boolean sendRenewalEmail(String to, String name, Double value, LocalDateTime renewalDate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedRenewalDate = renewalDate.format(formatter);

            helper.setSubject("Lembrete: Sua Associação Hallel Vence em Breve!");

            String html = """
                    <html>
                        <body style="font-family: Arial, sans-serif; color: #333;">
                            <div style="max-width: 600px; margin: auto; padding: 20px; background: #ebfff0; border: 1px solid #c9e6d0; border-radius: 10px;">
                                <h2 style="color: #4CAF50;">Não perca seus benefícios, %s! 🔔</h2>
                                <p style="font-size: 16px;">
                                    Agradecemos por fazer parte da nossa comunidade! Queremos lembrar que sua associação está programada para vencer em <b>%s</b>.
                                </p>
                                <p style="font-size: 16px;">
                                    Renove agora para garantir a continuidade de todos os seus benefícios e acesso.
                                </p>
                    
                                <h3 style="color: #333;">Detalhes da Renovação:</h3>
                                <ul style="list-style: none; padding: 0; font-size: 16px;">
                                    <li style="margin-bottom: 5px;"><strong>Valor da Mensalidade:</strong> R$ %.2f</li>
                                    <li style="margin-bottom: 5px;"><strong>Vencimento:</strong> %s</li>
                                </ul>
                    
                                <div style="text-align: center; margin-top: 30px;">
                                    <a href="[LINK PARA PÁGINA DE RENOVAÇÃO]" 
                                       style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                                        Renovar Agora
                                    </a>
                                </div>
                    
                                <p style="font-size: 14px; color: #888; margin-top: 30px;">
                                    Se você já renovou recentemente, por favor, desconsidere esta mensagem.
                                </p>
                                <br/>
                                <p style="font-size: 14px; color: #888;">Equipe de Associação Hallel 💚</p>
                            </div>
                        </body>
                    </html>
                    """.formatted(name, formattedRenewalDate, value, formattedRenewalDate);

            helper.setText(html, true);
            mailSender.send(message);

            log.info("Email de renovação enviado para: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar email de renovação para {}: {}", to, e.getMessage());
            return false;
        }
    }
    public Boolean sendComprovantEventParticipation(String to, String name,
                                                    LocalDateTime eventDate,
                                                    String eventTitle,
                                                    String eventId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedEventDate = eventDate.format(formatter);

            helper.setSubject("Comprovante de Participação no Evento: " + eventTitle);

            String html = """
            <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <div style="max-width: 600px; margin: auto; padding: 20px; background: #f0f9ff; border: 1px solid #b3e5fc; border-radius: 10px;">
                        <h2 style="color: #0288d1;">Olá, %s! ✅</h2>
                        <p style="font-size: 16px;">
                            Este é o seu comprovante de participação no evento <b>%s</b>.
                        </p>
                        <h3 style="color: #0288d1;">Detalhes do Evento:</h3>
                        <ul style="list-style: none; padding: 0; font-size: 16px;">
                            <li style="margin-bottom: 5px;"><strong>Data e Hora:</strong> %s</li>
                        </ul>
                        
                        <p style="font-size: 16px;">
                            Guarde este comprovante para referência futura. Obrigado por participar do evento!
                        </p>
                        
                        <div style="text-align: center; margin-top: 30px;">
                            <a href="https://comunidadecatolicahallel.com.br/evento/%s" 
                               style="background-color: #0288d1; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                                Ver Evento
                            </a>
                        </div>
                        
                        <p style="font-size: 14px; color: #888; margin-top: 30px;">
                            Se você tiver alguma dúvida sobre sua participação, entre em contato com nossa equipe.
                        </p>
                        <br/>
                        <p style="font-size: 14px; color: #888;">Equipe Hallel 💚</p>
                    </div>
                </body>
            </html>
            """.formatted(name, eventTitle, formattedEventDate,eventId);

            helper.setText(html, true);
            mailSender.send(message);

            log.info("Comprovante de participação enviado para: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar comprovante para {}: {}", to, e.getMessage());
            return false;
        }
    }

}
