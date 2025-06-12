package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.dto.DanceResponseShort;
import br.hallel.relational.api.app.ministry.model.DanceMinistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DanceRepository extends JpaRepository<DanceMinistry, UUID> {
    @Query("select new br.hallel.relational.api.app.ministry.dto.DanceResponseShort(dm.id, dm.name, dm.description, dm.link) from dance_ministry dm where dm.ministry.id = :ministryId")
    Page<DanceResponseShort> listAllDanceOfMinistry(@Param("ministryId") UUID ministryId, Pageable pageable);
}
