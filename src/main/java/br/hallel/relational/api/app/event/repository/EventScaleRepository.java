package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.dto.NotConfirmedScaleMinistryWithInfos;
import br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse;
import br.hallel.relational.api.app.event.dto.SimpleScaleResponse;
import br.hallel.relational.api.app.event.model.EventScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventScaleRepository extends JpaRepository<EventScale, UUID> {
    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    CAST(es.id AS string),
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                        CAST(e.id AS string), e.title, e.date, e.image_url, e.banner_url
                    ),
                    CAST(m.id AS string),
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
                    CAST(e.id AS string), e.title, e.date, e.image_url, e.banner_url
                ),
                m.id,
                e.date
            )
            FROM EventScale es JOIN es.event e JOIN es.ministry m 
            WHERE :memberId IN elements(es.membersMinistryInvitedIds) AND e.date BETWEEN :start AND :end """)
    List<ScaleEventWithEventInfoResponse> findAllByMemberIdAndDateBetween(
            @Param("memberId") UUID memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.SimpleScaleResponse(
                    es.id,
                    e.date
                )
                FROM EventScale es
                JOIN es.event e
                WHERE CAST(:membroId AS String) IN elements(es.membersMinistryInvitedIds)
                  AND e.date BETWEEN :start AND :end
            """)
    List<SimpleScaleResponse> findEscalaMinisterioIdsByMembroIdCanPaticipate(
            @Param("membroId") UUID membroId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    es.id,
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                       CAST(e.id AS string), e.title, e.date, e.image_url, e.banner_url
                    ),
                    m.id,
                    e.date
                )
                FROM EventScale es
                JOIN es.event e
                JOIN es.ministry m
                WHERE :membroId IN elements(es.membersMinistryConfirmeds)
                  AND e.date BETWEEN :start AND :end
            """)
    List<ScaleEventWithEventInfoResponse> findConfirmedScalesByMemberAndDateRange(
            @Param("membroId") UUID membroId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.SimpleScaleResponse(
                    es.id,
                    e.date
                )
                FROM EventScale es
                JOIN es.event e
                WHERE :membroId IN elements(es.membersMinistryConfirmeds)
                  AND e.date BETWEEN :start AND :end
            """)
    List<SimpleScaleResponse> findScalesConfirmedByMemberIdAndDateRange(
            @Param("membroId") UUID membroId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    es.id,
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                    CAST(e.id AS string), e.title, e.date, e.image_url, e.banner_url
            
                    ),
                    m.id,
                    e.date
                )
                FROM EventScale es
                JOIN es.event e
                JOIN es.ministry m
                WHERE e.date BETWEEN :start AND :end
            """)
    List<ScaleEventWithEventInfoResponse> findScalesInRangeDate(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("""
                SELECT new br.hallel.relational.api.app.event.dto.ScaleEventWithEventInfoResponse(
                    es.id,
                    new br.hallel.relational.api.app.event.dto.EventShortResponse(
                    CAST(e.id AS string), e.title, e.date, e.image_url, e.banner_url
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
                        CAST(e.id AS string), e.title, e.date, e.image_url, e.banner_url
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
                SELECT new br.hallel.relational.api.app.event.dto.NotConfirmedScaleMinistryWithInfos(
                    a.id,
                    a.membro,
                    a.escala.id,
                    a.reason
                )
                FROM NotConfirmedScaleMinistry a
                WHERE a.escala.id = :idEscala
            """)
    List<NotConfirmedScaleMinistryWithInfos> findReasonsAbsenceByEscalaId(@Param("idEscala") UUID idEscala);

    List<EventScale> findByRepertoryIdsContaining(UUID idRepertory);
}
