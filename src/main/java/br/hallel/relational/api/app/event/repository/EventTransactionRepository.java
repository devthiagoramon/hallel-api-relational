package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.dto.EventTransactionResponse;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventTransaction;
import br.hallel.relational.api.app.event.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventTransactionRepository
        extends JpaRepository<EventTransaction, UUID> {
    List<EventTransaction> findByEventId(UUID eventId);

    List<EventTransaction> findAllByEvent_Id(UUID eventId);

    List<EventTransaction> findByEventIdAndTransactionType(UUID eventId, TransactionType transactionType);

    Optional<EventTransaction> findByMercadoPagoPaymentId(Long mercadoPagoPaymentId);
}
