package br.hallel.relational.api.app.user.repository;

import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.dto.UserShortResponse;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<UserProfileResponse> findAllByNameContainingIgnoreCase(
            String name, Pageable pageable);

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
}
