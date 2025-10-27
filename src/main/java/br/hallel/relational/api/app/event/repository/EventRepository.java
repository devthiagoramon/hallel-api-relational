package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.dto.EventShortResponse;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventStatus;
import br.hallel.relational.api.app.event.model.EventType;
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
                    e.id, e.title, e.date, e.image_url, e.banner_url, e.itsFree, e.eventType,e.local_event_name,
                                e.eventStatus
                )
                FROM Event e
                WHERE e.id = :idEvento
            """)
    Optional<EventShortResponse> findByIdShort(@Param("idEvento") UUID idEvento);


    @Query("""
            SELECT e
            FROM Event e
            LEFT JOIN FETCH e.scales
            WHERE e.id = :eventId
            """)
    Optional<Event> listByIdWithMinistryResponse(@Param("eventId") UUID id);

    @Query("""
            SELECT e FROM Event e
            WHERE e.date > :date
            """)
    Page<Event> findAllUpcomingEvents(@Param("date") Date date, Pageable pageable);


    Page<Event> findAllByEventTypeOrderByTitleAsc(EventType eventType, Pageable pageable);

    List<Event> findByEventStatusNot(EventStatus eventStatus);

    List<Event> findByDateBetweenOrderByDateAsc(
            Date startOfDay,
            Date endOfDay
    );



    Page<Event> findByEventStatusNot(EventStatus eventStatus, Pageable pageable);

    Page<Event> findByEventStatus(EventStatus eventStatus, Pageable pageable);

    Page<Event> findByEventStatusNotOrderByTitleAsc(EventStatus eventStatus, Pageable pageable);

    Page<Event> findAllByEventStatusAndEventType(EventStatus eventStatus, EventType eventType, Pageable pageable);

    Page<Event> findByEventStatusNotOrderByDateAsc(EventStatus eventStatus, Pageable pageable);

    Page<Event> findByEventStatusOrderByTitleAsc(EventStatus eventStatus, Pageable pageable);

}
