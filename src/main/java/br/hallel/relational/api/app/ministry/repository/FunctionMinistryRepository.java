package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.dto.FunctionMinistryResponse;
import br.hallel.relational.api.app.ministry.model.FunctionMinistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionMinistryRepository
        extends JpaRepository<FunctionMinistry, UUID> {

    @Query("""
            select new br.hallel.relational.api.app.ministry.dto.FunctionMinistryResponse(fm.id, fm.ministryId, fm.name, fm.description, fm.color, fm.icon)
            from function_ministry fm
            where fm.ministryId = :ministry_id
            """
    )
    List<FunctionMinistryResponse> listAllFunctionsByMinistryId(
            @Param("ministry_id") UUID ministryId);

    @Query("""
            select fm from function_ministry fm where fm.id = :id
            """
    )
    Optional<FunctionMinistry> listById(
            @Param("id") UUID functionMinistryId);

}
