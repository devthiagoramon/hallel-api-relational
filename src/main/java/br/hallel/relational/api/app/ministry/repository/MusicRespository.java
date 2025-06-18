package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.dto.MusicWithoutMinistryResponse;
import br.hallel.relational.api.app.ministry.model.MusicMinistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MusicRespository extends JpaRepository<MusicMinistry, UUID> {

    @Query("""
            select new br.hallel.relational.api.app.ministry.dto.MusicWithoutMinistryResponse(
                       mum.id,
                       mum.name,
                       mum.description,
                       mum.letter,
                       mum.link
                       ) from MusicMinistry mum
            where mum.ministry.id = :ministryId
            """)
    Page<MusicWithoutMinistryResponse> listByMinistryId(@Param("ministryId") UUID ministryId, Pageable pageable);

    List<MusicMinistry> findAllByMinistry_Id(UUID ministryId);
}
