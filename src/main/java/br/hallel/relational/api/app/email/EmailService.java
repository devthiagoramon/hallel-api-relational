package br.hallel.relational.api.app.email;

import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String from;

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

            return true;
        } catch (Exception e) {
            System.out.println("Erro ao enviar email HTML: " + e.getMessage());
            return false;
        }
    }
}
