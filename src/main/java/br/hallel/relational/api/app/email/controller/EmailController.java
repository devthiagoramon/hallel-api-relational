package br.hallel.relational.api.app.email.controller;

import br.hallel.relational.api.app.email.service.EmailService;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/public/email")
@RequiredArgsConstructor
public class EmailController {

    private final EventParticipationRepository eventParticipationRepository;
    private final EmailService emailService;

    @PostMapping("/send-confirmation")
    @Transactional
    public ResponseEntity<String> sendConfirmationEmail(
            @RequestParam UUID eventId
    ) {

        List<EventParticipation> allByEventId =
                this.eventParticipationRepository.findAllByEvent_Id(eventId);
        for (EventParticipation user : allByEventId) {
            emailService.sendComprovantEventParticipation(
                    user.getEmail(),
                    user.getName(),
                    user.getEvent().getDate().toInstant().atZone(
                            ZoneId.systemDefault()
                    ).toLocalDateTime(),
                    user.getEvent().getTitle(),
                    user.getEvent().getId().toString(),
                    user.getEvent().getWhatsAppGroupLink()
            );

        }
        return ResponseEntity.ok("Emails enviados com sucesso!");
    }

    @PostMapping("/send-remind")
    public ResponseEntity<String> sendRemindEmailParticipation(
            @RequestParam UUID eventId
    ){
        List<EventParticipation> allByEventId =
                this.eventParticipationRepository.findAllByEvent_Id(eventId);
        for (EventParticipation user : allByEventId) {
            boolean isPaid = user.getStatusPaymentEventParticipation() ==
                    StatusPaymentEventParticipation.PAGO;
            log.info(
                    "Enviando email de lembrete para {} sobre o evento {}. Pago: {}",
                    user.getEmail(),
                    user.getEvent().getTitle(),
                    isPaid
            );
            emailService.sendEventParticipationReminderEmail(
                    user.getEmail(),
                    user.getName(),
                    user.getEvent().getDate().toInstant().atZone(
                            ZoneId.systemDefault()
                    ).toLocalDateTime(),
                    user.getEvent().getTitle(),
                    user.getEvent().getId().toString(),
                    false,
                    isPaid
            );

        }
        return ResponseEntity.ok("Notificação enviada!");
    }
}
