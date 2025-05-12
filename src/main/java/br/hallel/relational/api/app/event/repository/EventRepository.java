package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    Page<Event> findAllByOrderByTitleAsc(Pageable pageable);
    Page<Event> findAllByOrderByDateAsc(Pageable pageable);
}
