package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.ministry.model.Ministry;
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

    @Query("SELECT e FROM Ministry m JOIN m.eventScalesList e WHERE m.id = :id")
    List<EventScale> findAllEventScalesByMinistryId(
            @Param("id") UUID memberMinistryId);

    @Query("SELECT e FROM Ministry m JOIN m.eventScalesList e " +
            "WHERE m.id = :id AND e.date BETWEEN :startDate AND :endDate")
    List<EventScale> findAllEventScalesByMinistryIdAndDateRange(
            @Param("id") UUID ministryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
