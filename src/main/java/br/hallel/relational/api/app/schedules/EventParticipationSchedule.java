package br.hallel.relational.api.app.schedules;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @Scheduled(cron = "0 0 * * * *", zone = "America/Manaus")
    public void checkIfTheEventHasEnded() {
        log.info("Starting scheduled check for ended events at {}", new Date());

        List<Event> events = eventRepository.findByDateBeforeAndHasEndedFalse(new Date());
        log.info("Found {} events that have ended", events.size());

        List<EventParticipation> participationsToUpdate = new ArrayList<>();
        List<Event> eventsEnded = new ArrayList<>();

        if (events.isEmpty()) {
            return;
        }
        for (Event event : events) {
            List<EventParticipation> allParticipations = eventParticipationRepository.findAllByEvent_Id(event.getId());
            log.info("Processing event '{}' (id: {}) with {} participations", event.getTitle(), event.getId(), allParticipations.size());

            for (EventParticipation participation : allParticipations) {
                if (participation.getStatusPaymentEventParticipation() != StatusPaymentEventParticipation.PAGO) {
                    participation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.NAO_PAGO);

                }
                participation.setHasParticipated(true);
                participationsToUpdate.add(participation);
            }
            event.setHasEnded(true);
            eventsEnded.add(event);
        }
        this.eventParticipationRepository.saveAll(participationsToUpdate);
        this.eventRepository.saveAll(eventsEnded);
        log.info("Updated {} event participations", participationsToUpdate.size());
        log.info("Finished scheduled check for ended events at {}", new Date());
    }
}
