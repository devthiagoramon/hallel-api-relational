package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse;
import br.hallel.relational.api.app.ministry.model.AuditionMinistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditionRepository extends JpaRepository<AuditionMinistry, UUID>{
    List<AuditionMinistry> findAllByMinistry_Id(UUID ministryId);
    AuditionMinistry findByMinistry_Id(UUID ministryId);
    @Query("SELECT new br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse(e.id, e.date)  AS date " +
            "FROM EventScale e " +
            "WHERE e.ministry.id = :ministryId AND e.date >= :from " +
            "ORDER BY e.date ASC")
    List<EventScaleSimpleResponse> findAllEventScalesThatCanBeAddedIntoAudition(
            @Param("ministryId") UUID ministryId,
            @Param("from") LocalDateTime from
    );
}
