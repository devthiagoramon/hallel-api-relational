package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.FoodSaleItem;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentFood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FoodSaleItemRepository extends JpaRepository<FoodSaleItem, UUID> {
    Page<FoodSaleItem> findAllByEvent_Id(UUID eventId, Pageable pageable);

    Page<FoodSaleItem> findAllByEvent_IdAndTransaction_Status(UUID eventId, StatusPaymentFood transactionStatus, Pageable pageable);

    long countByFood_Id(UUID foodId);
}
