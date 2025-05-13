package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.dto.EventShortResponse;
import br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse;
import br.hallel.relational.api.app.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    Page<Event> findAllByOrderByTitleAsc(Pageable pageable);

    Page<Event> findAllByOrderByDateAsc(Pageable pageable);

    @Query("SELECT e.id AS id, e.title AS title, e.date AS date, e.image_url AS image_url, e.banner_url AS banner_url FROM Event e WHERE e.id = :idEvento")
    Optional<EventShortResponse> findByIdShort(@Param("idEvento") UUID idEvento);


}
