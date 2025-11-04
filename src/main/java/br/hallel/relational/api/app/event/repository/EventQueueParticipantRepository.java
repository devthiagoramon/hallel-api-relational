package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.EventQueueParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventQueueParticipantRepository extends JpaRepository<EventQueueParticipant, UUID> {
    @Query("""
                SELECT COUNT(eqp)
                FROM EventQueueParticipant eqp
                WHERE eqp.event.id = :eventId 
                AND eqp.queuedAt < :queuedAt
            """)
    Long countParticipantsBefore(
            @Param("eventId") UUID eventId,
            @Param("queuedAt") OffsetDateTime queuedAt
    );

    Optional<EventQueueParticipant> findFirstByEvent_IdOrderByQueuedAtAsc(UUID eventId);
}
