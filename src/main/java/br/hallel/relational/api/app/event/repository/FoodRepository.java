package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.dto.EventFoodTableResponseDTO;
import br.hallel.relational.api.app.event.model.Foods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FoodRepository extends JpaRepository<Foods, UUID> {

    Page<Foods> findAllByEvent_Id(UUID eventId, Pageable pageable);

    @Query(value = "SELECT new br.hallel.relational.api.app.event.dto.EventFoodTableResponseDTO(" +
            "f.id, f.name, f.value, f.stockQuantity, COUNT(fsi.id), f.registeredDate, f.event.id) " +
            "FROM Foods f " +
            "LEFT JOIN f.saleItems fsi " +
            "WHERE f.event.id = :eventId " +
            "GROUP BY f.id",
            countQuery = "SELECT count(f) FROM Foods f WHERE f.event.id = :eventId")
    Page<EventFoodTableResponseDTO> findFoodTableByEventId(@Param("eventId") UUID eventId, Pageable pageable);

    @Query(value = "SELECT new br.hallel.relational.api.app.event.dto.EventFoodTableResponseDTO(" +
            "f.id, f.name, f.value, f.stockQuantity, COUNT(fsi.id), f.registeredDate, f.event.id) " +
            "FROM Foods f " +
            "LEFT JOIN f.saleItems fsi " +
            "WHERE f.event.id = :eventId " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(fsi.id) DESC, f.name ASC")
    Page<EventFoodTableResponseDTO> findFoodTableByEventIdOrderBySales(@Param("eventId") UUID eventId, Pageable pageable);
}
