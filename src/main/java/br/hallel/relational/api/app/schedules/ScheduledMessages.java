package br.hallel.relational.api.app.schedules;

import br.hallel.relational.api.app.email.EmailService;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
public class ScheduledMessages {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "${spring.task.scheduling.cron}",
            zone = "America/Manaus")
    public void sendMessageToBirthDayPeople() {
        LocalDate today = LocalDate.now(ZoneId.of("America/Manaus"));
        List<User> users =
                userRepository.findByDayAndMonth(today.getDayOfMonth(), today.getMonthValue());
        for (User user : users) {
            if (user.getBirthdayEmailSentIn() == null
                    || !user.getBirthdayEmailSentIn().isEqual(today)) {
                emailService.sendBirthDayEmail(user.getEmail(),
                        "Feliz Aniversário, " + user.getName() + "!", user.getName(), user, today);

            }

        }
    }

}
