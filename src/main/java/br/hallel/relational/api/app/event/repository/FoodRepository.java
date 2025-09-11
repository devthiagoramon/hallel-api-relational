package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.Foods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FoodRepository extends JpaRepository<Foods, UUID> {

    Page<Foods> findAllByEvent_Id(UUID eventId, Pageable pageable);
}
