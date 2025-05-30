package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse;
import br.hallel.relational.api.app.event.dto.SimpleScaleResponse;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse;
import br.hallel.relational.api.app.ministry.model.Ministry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventScaleRepository extends JpaRepository<EventScale, UUID> {

    List<EventScale> findByEventId(UUID id);


    @Query("select new br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse(m.id, m.title, m.image) " +
            "from Ministry m join m.eventScalesList es " +
            "where es.event.id = :eventId")
    List<MinistrySimpleResponse> findMinistriesByEventId(@Param("eventId") UUID eventId);

    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    es.id,
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                        e.id, e.title, e.date, e.image_url, e.banner_url
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
                        e.id, e.title, e.date, e.image_url, e.banner_url
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

//    @Query("""
//                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
//                    es.id,
//                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
//                        e.id,
//                        e.title,
//                        e.date,
//                        e.image_url,
//                        e.banner_url
//                    ),
//                    m.id,
//                    e.date
//                )
//                FROM EventScale es
//                JOIN es.event e
//                JOIN es.ministry m
//                JOIN es.invitedMembers im
//                WHERE im.id = :memberId
//                  AND e.date BETWEEN :start AND :end
//            """)
//    List<ScaleEventWithEventInfoResponse> findScaleEventsWithInfoByMemberIdCanParticipate(
//            @Param("memberId") UUID memberId,
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end
//    );

//    @Query("""
//                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
//                    es.id,
//                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
//                        e.id,
//                        e.title,
//                        e.date,
//                        e.image_url,
//                        e.banner_url
//                    ),
//                    m.id,
//                    e.date
//                )
//                FROM EventScale es
//                JOIN es.event e
//                JOIN es.ministry m
//                WHERE :membroId IN elements(es.confirmedMembers)
//                  AND e.date BETWEEN :start AND :end
//            """)
//    List<ScaleEventWithEventInfoResponse> findConfirmedScalesByMemberAndDateRange(
//            @Param("membroId") UUID membroId,
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end
//    );

//    @Query("""
//                SELECT new br.hallel.relational.api.app.event.dto.SimpleScaleResponse(
//                    es.id,
//                    e.date
//                )
//                FROM EventScale es
//                JOIN es.event e
//                JOIN es.confirmedMembers cm
//                WHERE cm.id = :memberId
//                  AND e.date BETWEEN :start AND :end
//            """)
//    List<SimpleScaleResponse> findScaleIdsByMemberIdParticipate(
//            @Param("memberId") UUID memberId,
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end);
//
//    @Query("""
//                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
//                    es.id,
//                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
//                    e.id, e.title, e.date, e.image_url, e.banner_url
//
//                    ),
//                    m.id,
//                    e.date
//                )
//                FROM EventScale es
//                JOIN es.event e
//                JOIN es.ministry m
//                WHERE e.date BETWEEN :start AND :end
//            """)
//    List<ScaleEventWithEventInfoResponse> findScalesInRangeDate(
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end
//    );


    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    es.id,
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                    e.id AS UUID, e.title, e.date, e.image_url, e.banner_url
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
                        e.id, e.title, e.date, e.image_url, e.banner_url
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

}
