package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.EventFoodSales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventFoodSaleRepository extends JpaRepository<EventFoodSales, UUID> {
    Page<EventFoodSales> findAllByEvent_Id(UUID eventId, Pageable pageable);

    List<EventFoodSales> findByEventTransactionId(UUID eventTransactionId);
}
