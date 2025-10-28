package br.hallel.relational.api.app.user.repository;

import br.hallel.relational.api.app.user.dto.FilterAuthorietiesDTO;
import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.dto.UserShortResponse;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            LEFT JOIN u.roles r
            WHERE
            (
                :nameFiltered IS NULL\s
                OR :nameFiltered = ''\s
                OR u.name LIKE CONCAT('%', :nameFiltered, '%')
            )
            AND
            (
                :roleName IS NULL
                OR :roleName = 'ALL'
                OR r.description = :roleName
            )
            AND
            (
                :roleName IS NULL
                OR :roleName = 'ALL'
                OR :roleName != 'USER'
                OR
                (
                    :roleName = 'USER' AND SIZE(u.roles) = 1
                )
            )
            AND
            (
                :roleName IS NULL
                OR :roleName = 'ALL'
                OR :roleName != 'ASSOCIADO'
                OR
                (
                    :roleName = 'ASSOCIADO' AND SIZE(u.roles) = 2
                )
            )
            ORDER BY u.name ASC
            """)
    Page<User> searchAllByOrderByNameAsc(@Param("nameFiltered") String nameFiltered, @Param("roleName") String roleName, Pageable pageable);

    List<UserProfileResponse> findAllByNameContainingIgnoreCase(
            String name, Pageable pageable);

    @Query("""
                SELECT u
                FROM User u
                            JOIN u.roles r
                WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    Page<User> searchUserProfilesByName(@Param("name") String name, Pageable pageable);


    UUID id(UUID id);

    Optional<User> findByToken(String token);

    @Query("""
            SELECT new br.hallel.relational.api.app.user.dto.UserShortResponse(u.id, u.name, u.email)
            from User u
            where u.id not in (
                select mm.user.id from MemberMinistry mm where mm.ministry.id = :ministryId
            ) and not exists (
                select r from u.roles r where r.description = 'ADMIN'
            )
            """)
    Page<UserShortResponse> listUsersAddableInMinistry(
            @Param("ministryId") UUID ministryId, Pageable pageable);

    @Query(value = "SELECT * FROM \"user\" WHERE EXTRACT(DAY FROM date_birth) = :day AND EXTRACT(MONTH FROM date_birth) = :month", nativeQuery = true)
    List<User> findByDayAndMonth(@Param("day") int day, @Param("month") int month);

    @Query("""
                SELECT u FROM User u
                LEFT JOIN FETCH u.devicesUser d
                WHERE (
                  SELECT MAX(l.accessedAt)
                  FROM LastAccessLog l
                  WHERE l.user = u
                ) < :date
            """)
    List<User> findUsersWithLastAccessBefore(@Param("date") LocalDateTime date);

}
