package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.dto.EventTransactionResponse;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventTransactionRepository
        extends JpaRepository<EventTransaction, UUID> {
    List<EventTransaction> findByEventId(UUID eventId);

    List<EventTransaction> findAllByEvent_Id(UUID eventId);
}
