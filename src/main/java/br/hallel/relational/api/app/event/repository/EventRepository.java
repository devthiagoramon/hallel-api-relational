package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.EventType;
import br.hallel.relational.api.app.event.dto.EventShortResponse;
import br.hallel.relational.api.app.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    Page<Event> findAllByOrderByTitleAsc(Pageable pageable);

    Page<Event> findAllByDateGreaterThanEqualOrderByDateAsc(LocalDateTime date, Pageable pageable);

    List<Event> findAllByTitleContainingIgnoreCaseOrderByTitleAsc(String title, Pageable pageable);

    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.EventShortResponse(
                    e.id, e.title, e.date, e.image_url, e.banner_url, e.itsFree
                )
                FROM Event e
                WHERE e.id = :idEvento
            """)
    Optional<EventShortResponse> findByIdShort(@Param("idEvento") UUID idEvento);


    @Query("""
            SELECT e
            FROM Event e
            JOIN FETCH e.scales
            WHERE e.id = :eventId
            """)
    Optional<Event> listByIdWithMinistryResponse(@Param("eventId") UUID id);

    @Query("""
    SELECT e FROM Event e
    WHERE e.date > :date
    """)
    Page<Event> findAllUpcomingEvents( @Param("date") Date date, Pageable pageable);


    List<Event> findByDateBeforeAndHasEndedFalse(Date dateBefore);

    Page<Event> findAllByEventTypeOrderByTitleAsc(EventType eventType, Pageable pageable);

    Page<Event> findAllByHasEndedAndEventType(Boolean hasEnded, EventType type,Pageable pageable);
    Page<Event> findAllByHasEnded(Boolean hasEnded, Pageable pageable);
}
