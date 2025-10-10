package br.hallel.relational.api.app.schedules;

import br.hallel.relational.api.app.association.model.Associate;
import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import br.hallel.relational.api.app.association.repository.AssociateRepository;
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
    @Autowired
    private AssociateRepository associateRepository;

    private static final int DAYS_BEFORE_RENEWAL = 7;

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

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional // Garante que todas as alterações no loop sejam salvas juntas
    public void checkDelinquentAssociates() {
        log.info("Iniciando a verificação de associados inadimplentes...");
        LocalDateTime today = LocalDateTime.now();

        // 1. Busca associados que estão ATIVOS, mas cuja renovação expirou
        // Este é um método Customizado que você adiciona ao seu AssociateRepository
        List<Associate> expiredAssociates = associateRepository.findByStatusAndRenewalDateBefore(
                AssociatePaymentStatus.PAGO,
                today
        );

        if (expiredAssociates.isEmpty()) {
            log.info("Nenhum associado encontrado com renovação expirada.");
            return;
        }

        log.warn("Foram encontrados {} associados em atraso.", expiredAssociates.size());


        // 2. Processa cada inadimplente
        for (Associate associate : expiredAssociates) {
            log.info("Processando inadimplência para o Associado ID: {}", associate.getId());

            // Regra de Negócio: Marca como SUSPENSO
            associate.setStatus(AssociatePaymentStatus.SUSPENSO);

            emailService.sendBillingAssociate(
                    associate.getUser().getEmail(),
                    "Atenção: Sua Associação Hallel Foi Suspensa",
                    associate.getUser().getName(),
                    associate.getValueAssociation(),
                    associate.getRenewalDate()
            );
        }

        // 3. Salva todas as alterações (graças ao @Transactional, isso ocorre ao final)
        associateRepository.saveAll(expiredAssociates);

        log.info("Verificação de inadimplentes concluída. {} status atualizados para SUSPENSO.", expiredAssociates.size());
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void checkUpcomingRenewals() {
        log.info("Iniciando a verificação de renovações próximas...");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Manaus"));
        LocalDateTime nextWeek = now.plusDays(DAYS_BEFORE_RENEWAL);

        List<Associate> renewalCandidates = associateRepository.findByStatusAndRenewalDateBetween(
                AssociatePaymentStatus.PAGO,
                now,
                nextWeek
        );

        if (renewalCandidates.isEmpty()) {
            log.info("Nenhuma renovação próxima nos próximos {} dias.", DAYS_BEFORE_RENEWAL);
            return;
        }

        log.info("Foram encontrados {} associados próximos da renovação.", renewalCandidates.size());

        for (Associate associate : renewalCandidates) {
            log.info("Enviando aviso de renovação para o Associado ID: {}", associate.getId());

            this.emailService.sendRenewalEmail(
                    associate.getUser().getEmail(),
                    associate.getUser().getName(),
                    associate.getValueAssociation(),
                    associate.getRenewalDate()
            );

        }
    }

}
