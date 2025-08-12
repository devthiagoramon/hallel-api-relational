package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, UUID> {
    boolean existsByUserAndEvent(User user, Event event);
}
