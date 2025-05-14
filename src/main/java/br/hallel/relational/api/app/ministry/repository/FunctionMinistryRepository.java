package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.dto.FunctionMinistryResponse;
import br.hallel.relational.api.app.ministry.model.FunctionMinistry;
import br.hallel.relational.api.app.ministry.model.FunctionMinistryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionMinistryRepository extends JpaRepository<FunctionMinistry, FunctionMinistryId> {

    @Query(
            value = """
                    select * from function_ministry fm where fm.ministry_id = :ministry_id
                    """, nativeQuery = true
    )
    List<FunctionMinistryResponse> listAllFunctionsByMinistryId(@Param("ministry_id") UUID ministryId);

    @Query(
            value = """
                    select * from function_ministry fm where fm.id = :id
                    """, nativeQuery = true
    )
    Optional<FunctionMinistry> listById(@Param("id") UUID functionMinistryId);
}
