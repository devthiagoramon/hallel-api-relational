package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantResponse;
import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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
                JOIN scp.eventScale eventScale
                WHERE eventScale.id = :scaleId
            """)
    List<ScaleChatParticipant> listParticipantsOfScale(@Param("scaleId") UUID eventScaleId);


    @Query("""
                select scp from ScaleChatParticipant scp
                JOIN fetch scp.memberEventScale mes
                join fetch mes.memberMinistry mm
                join fetch mm.user u
                WHERE scp.id = :scaleChatParticipantId
            """)
    Optional<ScaleChatParticipant> listByIdWithUserInfo(@Param("scaleChatParticipantId") UUID scaleChatParticipantId);

    boolean existsScaleChatParticipantByEventScale_IdAndMemberEventScale_MemberMinistry_User_Id(UUID eventScaleId, UUID memberEventScaleMemberMinistryUserId);

    @Query("""
                select new br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantResponse(
                    scp.id,
                    u
                    ) from ScaleChatParticipant scp
                JOIN scp.eventScale eventScale
                JOIN scp.memberEventScale mes
                join mes.memberMinistry mm
                join mm.user u
                WHERE eventScale.id = :scaleId
            """)
    List<ScaleChatParticipantResponse> listParticipantsWithUserModelByScaleId(@Param("scaleId") UUID scaleId);

    Optional<ScaleChatParticipant> findScaleChatParticipantsByEventScale_IdAndMemberEventScale_MemberMinistry_User_Id(UUID eventScaleId, UUID memberEventScaleMemberMinistryUserId);

    @Query("""
                select new br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantResponse(
                    scp.id,
                    u
                    ) from ScaleChatParticipant scp
                JOIN scp.eventScale eventScale
                JOIN scp.memberEventScale mes
                join mes.memberMinistry mm
                join mm.user u
                WHERE eventScale.id = :scaleId AND scp.id <> :memberSenderId
            """)
    List<ScaleChatParticipantResponse> listParticipantsOfScaleWhoNotSender(@Param("scaleId") UUID scaleId, @Param("memberSenderId") UUID memberSenderId);
}
