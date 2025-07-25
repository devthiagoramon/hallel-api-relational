package br.hallel.relational.api.app.schedules;

import br.hallel.relational.api.app.email.EmailService;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import br.hallel.relational.api.app.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class ScheduledMessages {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "${spring.task.scheduling.cron}", zone = "America/Manaus")
    @Transactional
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

    @Scheduled(cron = "0 0 0,12 * * *", zone = "America/Manaus")
    public void sendReminderToPeople() {
        Calendar calWeek = Calendar.getInstance();
        calWeek.add(Calendar.DAY_OF_YEAR, -7);
        LocalDateTime oneWeekAgo = calWeek.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        List<User> inactiveUsers = userRepository.findUsersWithLastAccessBefore(oneWeekAgo);

        for (User user : inactiveUsers) {
            this.userService.sendNotificationOfMissingUsers(user);
        }
    }
}
