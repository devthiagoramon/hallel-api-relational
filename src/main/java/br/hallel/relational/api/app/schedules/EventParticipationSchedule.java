package br.hallel.relational.api.app.schedules;

import br.hallel.relational.api.app.email.service.EmailService;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.EventStatus;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventParticipationSchedule {

    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 30 * * * *", zone = "America/Manaus")
    @Transactional
    public void updateEventStatus() {
        log.info("Iniciando verificação de status dos eventos...");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Manaus"));
        List<Event> activeEvents = eventRepository.findByEventStatusNot(EventStatus.FINALIZADO);
        log.info("Encontrados {} eventos ativos para verificação.", activeEvents.size());

        List<Event> eventsToUpdate = new ArrayList<>();
        List<EventParticipation> participationsToUpdate = new ArrayList<>();
        try {

            for (Event event : activeEvents) {

                LocalDateTime startTime = event.getDate()
                        .toInstant()
                        .atZone(ZoneId.of("America/Manaus"))
                        .toLocalDateTime();
                LocalDateTime endTime;
                if (event.getDuration() != null) {
                    endTime = startTime.plus(event.getDuration());
                } else {
                    endTime = startTime.plusHours(10);
                }
                LocalDateTime bufferedEndTime = endTime.plusHours(1);

                if (now.isAfter(bufferedEndTime)) {
                    event.setEventStatus(EventStatus.FINALIZADO);
                    eventsToUpdate.add(event);
                    log.info("Evento '{}' (id: {}) finalizado. Status alterado para FINALIZADO.", event.getTitle(),
                            event.getId());

                    List<EventParticipation> allParticipations = eventParticipationRepository.findAllByEvent_Id(
                            event.getId());
                    for (EventParticipation participation : allParticipations) {
                        if (participation.getStatusPaymentEventParticipation() != StatusPaymentEventParticipation.PAGO) {
                            participation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.NAO_PAGO);
                        }
                        participation.setHasParticipated(true);
                        participationsToUpdate.add(participation);
                    }

                } else if (now.isAfter(startTime) && event.getEventStatus() == EventStatus.AGENDADO) {
                    event.setEventStatus(EventStatus.OCORRENDO);
                    eventsToUpdate.add(event);
                    log.info("Evento '{}' (id: {}) iniciado. Status alterado para EM_ANDAMENTO.", event.getTitle(),
                            event.getId());
                }
            }

            if (!eventsToUpdate.isEmpty()) {
                eventRepository.saveAll(eventsToUpdate);
                log.info("{} eventos tiveram seus status atualizados.", eventsToUpdate.size());
            }
            if (!participationsToUpdate.isEmpty()) {
                eventParticipationRepository.saveAll(participationsToUpdate);
                log.info("{} participações foram atualizadas para refletir o fim dos eventos.",
                        participationsToUpdate.size());
            }

            log.info("Verificação de status de eventos finalizada.");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Erro ao tentar atualizar eventos.");
            log.error(e.toString());
            log.error(e.getCause().toString());
        }
    }

    @Scheduled(cron = "0 0 * * * *", zone = "America/Manaus") // Executa todo início de hora
    @Transactional
    public void sendEmailRemindEventParticipation() {
        ZoneId zone = ZoneId.of("America/Manaus");
        LocalDateTime now = LocalDateTime.now(zone);

        // Início e fim do dia atual
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        Date startDate = Date.from(startOfDay.atZone(zone).toInstant());
        Date endDate = Date.from(endOfDay.atZone(zone).toInstant());

        // Busca todos os eventos que ocorrem HOJE
        List<Event> todayEvents = eventRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);

        if (todayEvents.isEmpty()) {
            log.info("Nenhum evento encontrado para hoje ({})", now.toLocalDate());
            return;
        }

        for (Event event : todayEvents) {
            LocalDateTime eventTime = event.getDate().toInstant().atZone(zone).toLocalDateTime();

            Duration untilEvent = Duration.between(now, eventTime);

            // Caso 1: início do dia → mandar lembrete geral
            if (now.getHour() == 6) {
                sendReminderForEvent(event, true);
            }

            if (!untilEvent.isNegative() && untilEvent.toMinutes() <= 60 && untilEvent.toMinutes() >= 0) {
                sendReminderForEvent(event, false);
            }
        }
    }

    private void sendReminderForEvent(Event event, boolean isMorningReminder) {
        List<EventParticipation> participants = eventParticipationRepository.findAllByEvent_Id(event.getId());

        for (EventParticipation participant : participants) {
            this.emailService.sendEventParticipationReminderEmail(
                    participant.getEmail(),
                    participant.getName(),
                    event.getDate().toInstant().atZone(ZoneId.of("America/Manaus")).toLocalDateTime(),
                    event.getTitle(),
                    event.getId().toString(),
                    isMorningReminder
            );
        }
    }

}

