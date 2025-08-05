package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScaleChatParticipantRepository extends JpaRepository<ScaleChatParticipant, UUID> {
    List<ScaleChatParticipant> findScaleChatParticipantsByEventScale_Id(UUID eventScaleId);

    @Query("""
    select scp from ScaleChatParticipant scp
    JOIN fetch scp.memberEventScale mes
    join fetch mes.memberMinistry mm
    join fetch mm.user u
    join fetch u.devicesUser
""")
    List<ScaleChatParticipant> listParticipantsOfScale(@Param("scaleId") UUID eventScaleId);
}
