package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, UUID> {
    boolean existsByUserAndEvent(User user, Event event);

    List<EventParticipation> findAllByEvent_Id(UUID eventId);
    List<EventParticipation> findAllByEvent_IdAndStatusPaymentEventParticipation(UUID eventId,
                                                                                 StatusPaymentEventParticipation status);

    List<EventParticipation> findAllByUser_Id(UUID userId);

    Optional<EventParticipation> findByUser_IdAndEvent_Id(UUID userId, UUID eventId);

    List<EventParticipation> findByStatusPaymentEventParticipation(StatusPaymentEventParticipation statusPaymentEventParticipation);
    Optional<EventParticipation> findByPixTxid(String pixTxid);

    UUID user(User user);
}
