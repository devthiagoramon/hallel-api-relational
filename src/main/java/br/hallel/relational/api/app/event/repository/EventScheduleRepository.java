package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.EventSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventScheduleRepository extends JpaRepository<EventSchedule, UUID> {
}
