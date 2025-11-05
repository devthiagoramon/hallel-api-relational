package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.UserFunctionInEvent;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, UUID> {
    boolean existsByUserAndEvent(User user, Event event);

    List<EventParticipation> findAllByEvent_Id(UUID eventId);

    Page<EventParticipation> findAllByEvent_Id(UUID eventId, Pageable pageable);

    List<EventParticipation> findAllByEvent_IdAndStatusPaymentEventParticipation(UUID eventId,
                                                                                 StatusPaymentEventParticipation status);
    Page<EventParticipation> findAllByEvent_IdAndStatusPaymentEventParticipation(UUID eventId,
                                                                                 StatusPaymentEventParticipation status,
                                                                                 Pageable pageable);

    List<EventParticipation> findAllByUser_Id(UUID userId);

    Page<EventParticipation> findAllByUser_Id(UUID userId, Pageable pageable);

    Optional<EventParticipation> findByUser_IdAndEvent_Id(UUID userId, UUID eventId);

    List<EventParticipation> findByStatusPaymentEventParticipation(
            StatusPaymentEventParticipation statusPaymentEventParticipation);

    Optional<EventParticipation> findByPixTxid(String pixTxid);

    UUID user(User user);

    Optional<EventParticipation> findByMercadoPagoPaymentId(Long mercadoPagoPaymentId);

    Optional<EventParticipation> findByUserAndEvent(User user, Event event);

    @Query("""
                    SELECT u from User u
                    JOIN u.roles r
                    WHERE u.id
                            NOT IN (
                                SELECT ep.user.id FROM EventParticipation ep WHERE ep.event.id = :eventId AND ep.user IS NOT NULL
                            )
                    AND r.description = 'USER' AND SIZE(u.roles) = 1
            """)
    Page<User> listUsersWhoNotParticipateOfEvent(@Param("eventId") UUID eventId, Pageable page);

    @Query("""
                SELECT u from User u
                JOIN u.roles r
                WHERE u.id NOT IN (
                                SELECT ep.user.id FROM EventParticipation ep WHERE ep.event.id = :eventId
                                )
                AND LOWER(u.name) LIKE concat('%', LOWER(:name), '%') AND r.description = 'USER' AND SIZE(u.roles) = 1
            """)
    Page<User> listUsersWhoNotParticipateOfEventByName(@Param("eventId") UUID eventId, @Param("name") String name,
                                                       Pageable page);

    Page<EventParticipation> findAllByUser_IdAndEvent_DateGreaterThanEqualOrderByEvent_DateAsc(UUID userId,
                                                                                               LocalDateTime now,
                                                                                               Pageable pageable);

    Page<EventParticipation> findAllByUser_IdOrderByEvent_Title(UUID userId, Pageable pageable);

    Page<EventParticipation> findAllByUser_name(String userName, Pageable pageable);
    @Query("""
                SELECT ep from EventParticipation ep
                join ep.user u
                where ep.event.id = :eventId
                AND LOWER(u.name) LIKE concat('%', LOWER(:userName), '%')
            """)
    Page<EventParticipation> listAllByUser_nameAndEvent_id(@Param("userName") String userName,
                                                           @Param("eventId") UUID eventId, Pageable pageable);

    @Query("""
    SELECT ep from EventParticipation ep
    WHERE ep.user.id = :userId AND ep.event.id = :eventId AND ep.userFunctionInEvent = :userFunction
    """)
    Optional<EventParticipation> isFrenteCaixa(@Param("userId") UUID userId, @Param("eventId") UUID eventId, @Param("userFunction")
                                               UserFunctionInEvent userFunctionInEvent);

    List<EventParticipation> findAllByStatusPaymentEventParticipation(StatusPaymentEventParticipation statusPaymentEventParticipation);

    Optional<EventParticipation> findByEmailAndEvent_Id(String email, UUID eventId);

    long countByEvent_IdAndStatusPaymentEventParticipationNot(UUID eventId, StatusPaymentEventParticipation statusPaymentEventParticipation);
}
