package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.dto.EventScaleWithStatusInfos;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberEventScaleRepository extends JpaRepository<MemberEventScale, UUID> {
    List<MemberEventScale> findAllByStatusAndEventScale_Id(MemberEventScaleStatus status
            , UUID eventScaleId);

    MemberEventScale findByStatusAndEventScale_IdAndUser_Id(MemberEventScaleStatus status
            , UUID eventScaleId, UUID userId);

    Optional<MemberEventScale> findByUser_IdAndEventScale_Id(UUID userId, UUID eventScaleId);

    List<MemberEventScale> findAllByEventScale_Id(UUID eventScaleId);

    @Query(
            """
                    select es from EventScale es
                    join MemberEventScale mes on mes.eventScale.id = es.id
                    where user.id = :userId and es.ministry.id = :ministryId and es.date between :initial and :final and mes.status = br.hallel.relational.api.app.event.model.MemberEventScaleStatus.CONVIDADO
                    order by es.date asc
                    """
    )
    List<EventScale> listAllScaleWhoUserHasBeenInvitedByUserIdAndMinistryIdRangeDate(@Param("userId") UUID userId,
                                                                                     @Param("ministryId") UUID ministryId,
                                                                                     @Param("initial") Date initialDate,
                                                                                     @Param("final") Date finalDate);

    @Query(
            """
                    select new br.hallel.relational.api.app.event.dto.EventScaleWithStatusInfos(
                                        es.id,
                                        es.date,
                                        es.event,
                                        mes.status
                                        ) from EventScale es
                    join MemberEventScale mes on mes.eventScale.id = es.id
                    where user.id = :userId and es.ministry.id = :ministryId and es.date between :initial and :final
                    order by es.date asc
                    """
    )
    List<EventScaleWithStatusInfos> listAllScaleWithStatusInfosByUserIdAndMinistryIdRangeDate(UUID userId,
                                                                                              UUID ministryId,
                                                                                              @Param("initial") Date initialDate,
                                                                                              @Param("final") Date finalDate);

    void deleteMemberEventScaleByEventScale_IdAndUser_Id(UUID eventScaleId, UUID userId);

    UUID id(UUID id);
}
