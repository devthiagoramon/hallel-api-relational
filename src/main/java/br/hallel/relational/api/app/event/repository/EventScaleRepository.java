package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.dto.EventScaleWithRepertoriesResponse;
import br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse;
import br.hallel.relational.api.app.ministry.model.RepertoryMinistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventScaleRepository extends JpaRepository<EventScale, UUID> {

    List<EventScale> findByEventId(UUID id);


    @Query("""
                SELECT es
                FROM MemberEventScale mes
                JOIN mes.eventScale es
                JOIN mes.memberMinistry mm
                WHERE mm.user.id = :memberId
                  AND mes.status = :status
                  AND es.date BETWEEN :start AND :end
            """)
    List<EventScale> findEscalaMinisterioIdsByMembroIdParticipate(
            @Param("memberId") UUID memberId,
            @Param("status") MemberEventScaleStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("select new br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse(m.id, m.title, m.image, m.ministryType) " +
            "from Ministry m join m.eventScalesList es " +
            "where es.event.id = :eventId")
    List<MinistrySimpleResponse> findMinistriesByEventId(@Param("eventId") UUID eventId);

    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    es.id,
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                        e.id, e.title, e.date, e.image_url, e.banner_url, e.itsFree, e.eventType,
                                    e.local_event_name,
                                e.eventStatus
                    ),
                    m.id,
                    e.date
                )
                FROM EventScale es
                JOIN es.event e
                JOIN es.ministry m
            """)
    List<ScaleEventWithEventInfoResponse> findAllWithEventosInfos();

    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    es.id,
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                        e.id, e.title, e.date, e.image_url, e.banner_url, e.itsFree, e.eventType,
                                    e.local_event_name,
                                e.eventStatus
                    ),
                    m.id,
                    e.date
                )
                FROM EventScale es
                JOIN es.event e
                JOIN es.ministry m
                JOIN m.membersMinistry u
                WHERE u.id = :membroId
                AND e.date BETWEEN :start AND :end
            """)
    List<ScaleEventWithEventInfoResponse> findAllWithEventsInfosCanParticipateByMembroId(
            @Param("membroId") UUID membroId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT e.event FROM EventScale e WHERE e.id = :id")
    Event findEventByEventScaleId(@Param("id") UUID eventScaleId);


    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    es.id,
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                    e.id AS UUID, e.title, e.date, e.image_url, e.banner_url, e.itsFree, e.eventType,
                                e.local_event_name,
                                e.eventStatus
                    ),
                    m.id,
                    e.date
                )
                FROM EventScale es
                JOIN es.event e
                JOIN es.ministry m
                WHERE m.id = :idMinisterio
                  AND e.date BETWEEN :start AND :end
            """)
    List<ScaleEventWithEventInfoResponse> findScalesByMinistryIdAndDateRange(
            @Param("idMinisterio") UUID idMinisterio,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    es.id,
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                        e.id, e.title, e.date, e.image_url, e.banner_url, e.itsFree, e.eventType, e.local_event_name,
                                e.eventStatus
                    ),
                    m.id,
                    e.date
                )
                FROM EventScale es
                JOIN es.event e
                JOIN es.ministry m
                WHERE es.id = :idEscalaMinisterio
            """)
    ScaleEventWithEventInfoResponse findScaleByIdWithInfos(@Param("idEscalaMinisterio") UUID idEscalaMinisterio);


    @Query("""
                select es.repertories from EventScale es where es.id = :eventScaleId
            """)
    List<RepertoryMinistry> findRepertoriesOfEventScale(@Param("eventScaleId") UUID eventScaleId);

}
