package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.event.dto.EventResponse;
import br.hallel.relational.api.app.event.dto.EventSimpleResponse;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse;
import br.hallel.relational.api.app.ministry.model.Ministry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface MinistryRepository extends JpaRepository<Ministry, UUID> {

    @Query("SELECT e FROM EventScale e JOIN Ministry m on m.id = e.ministry.id where m = :ministryId")
    List<EventScale> findAllEventScalesByMinistryId(
            @Param("ministryId") UUID ministryId);

    @Query("SELECT new br.hallel.relational.api.app.event.dto.EventSimpleResponse(e.id,e.title, e.date) " +
            "FROM Event e " +
            "JOIN e.scales es " +
            "WHERE es.ministry.id = :ministryId")
    List<EventSimpleResponse> findAllEventsByMinistryId(@Param("ministryId") UUID ministryId);

    @Query("""
            SELECT e FROM EventScale e
            JOIN Ministry m on m.id = e.ministry.id
            WHERE m.id = :ministryId AND e.date BETWEEN :startDate AND :endDate
            ORDER BY e.date ASC
            """)
    List<EventScale> findAllEventScalesByMinistryIdAndDateRange(
            @Param("ministryId") UUID ministryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    List<Ministry> findAllByTitleContainingIgnoreCaseOrderByTitle(String title);

}
