package br.hallel.relational.api.app.email.controller;

import br.hallel.relational.api.app.email.dto.EmailParticipationDTO;
import br.hallel.relational.api.app.email.service.EmailAuthService;
import br.hallel.relational.api.app.email.service.EmailEventParticipationService;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
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
    private final EmailEventParticipationService emailEventParticipationService;
    private final EmailAuthService emailAuthService;

    @PostMapping("/send-confirmation")
    @Transactional
    public ResponseEntity<String> sendConfirmationEmail(
            @RequestParam UUID eventId
    ) {

        List<EventParticipation> allByEventId =
                this.eventParticipationRepository.findAllByEvent_Id(eventId);
        for (EventParticipation user : allByEventId) {
            EmailParticipationDTO emailDto = new EmailParticipationDTO(
                    user.getEmail(),
                    user.getName(),
                    user.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    user.getEvent().getTitle()
            );
            emailEventParticipationService.sendComprovantEventParticipation(
                    emailDto,
                    user.getEvent().getId().toString(),
                    user.getEvent().getWhatsAppGroupLink()
            );

        }
        return ResponseEntity.ok("Emails enviados com sucesso!");
    }

    @PostMapping("/send-remind")
    public ResponseEntity<String> sendRemindEmailParticipation(
            @RequestParam UUID eventId
    ) {
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
            EmailParticipationDTO emailDto = new EmailParticipationDTO(
                    user.getEmail(),
                    user.getName(),
                    user.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    user.getEvent().getTitle()
            );
            emailEventParticipationService.sendEventParticipationReminderEmail(
                    emailDto,
                    user.getEvent().getId().toString(),
                    false,
                    isPaid
            );

        }
        return ResponseEntity.ok("Notificação enviada!");
    }

    @PostMapping("/send-refund")
    public ResponseEntity<String> sendRefundEmailParticipation(
            @RequestParam UUID eventId
    ) {
        List<EventParticipation> allByEventId =
                this.eventParticipationRepository.findAllByEvent_Id(eventId);
        for (EventParticipation user : allByEventId) {
            boolean isPaid = user.getStatusPaymentEventParticipation() ==
                    StatusPaymentEventParticipation.PAGO;
            log.info(
                    "Enviando email de lembrete para {} sobre o evento {}. Estornado: {}",
                    user.getEmail(),
                    user.getEvent().getTitle(),
                    isPaid
            );
            EmailParticipationDTO emailDto = new EmailParticipationDTO(
                    user.getEmail(),
                    user.getName(),
                    user.getEvent().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    user.getEvent().getTitle()
            );
            emailEventParticipationService.sendRefundEventParticipation(
                    emailDto,
                    user.getAmountPaid()
            );

        }
        return ResponseEntity.ok("Notificação enviada!");
    }

    @PostMapping("/send-signup")
    public String sendSignUpEmail(@RequestParam String name,
                                  @RequestParam String email) {
        emailAuthService.sendSignUpMail(email, name);
        return "Email enviado";
    }

}
