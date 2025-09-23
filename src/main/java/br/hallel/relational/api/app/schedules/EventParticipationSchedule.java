package br.hallel.relational.api.app.schedules;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.EventStatus;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EventParticipationSchedule {

    @Autowired
    private EventParticipationRepository eventParticipationRepository;
    @Autowired
    private EventRepository eventRepository;

    @Scheduled(cron = "0 30 * * * *", zone = "America/Manaus")
    public void updateEventStatus() {
        log.info("Iniciando verificação de status dos eventos...");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Manaus"));
        List<Event> activeEvents = eventRepository.findByEventStatusNot(EventStatus.FINALIZADO);
        log.info("Encontrados {} eventos ativos para verificação.", activeEvents.size());

        List<Event> eventsToUpdate = new ArrayList<>();
        List<EventParticipation> participationsToUpdate = new ArrayList<>();

        for (Event event : activeEvents) {

            LocalDateTime startTime = event.getDate()
                    .toInstant()
                    .atZone(ZoneId.of("America/Manaus"))
                    .toLocalDateTime();

            LocalDateTime endTime = startTime.plus(event.getDuration());
            LocalDateTime bufferedEndTime = endTime.plusHours(1);

            if (now.isAfter(bufferedEndTime )) {
                event.setEventStatus(EventStatus.FINALIZADO);
                eventsToUpdate.add(event);
                log.info("Evento '{}' (id: {}) finalizado. Status alterado para FINALIZADO.", event.getTitle(), event.getId());

                List<EventParticipation> allParticipations = eventParticipationRepository.findAllByEvent_Id(event.getId());
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
                log.info("Evento '{}' (id: {}) iniciado. Status alterado para EM_ANDAMENTO.", event.getTitle(), event.getId());
            }
        }

        if (!eventsToUpdate.isEmpty()) {
            eventRepository.saveAll(eventsToUpdate);
            log.info("{} eventos tiveram seus status atualizados.", eventsToUpdate.size());
        }
        if (!participationsToUpdate.isEmpty()) {
            eventParticipationRepository.saveAll(participationsToUpdate);
            log.info("{} participações foram atualizadas para refletir o fim dos eventos.", participationsToUpdate.size());
        }

        log.info("Verificação de status de eventos finalizada.");
    }
}
