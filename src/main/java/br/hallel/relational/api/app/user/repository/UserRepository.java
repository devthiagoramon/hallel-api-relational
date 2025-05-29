package br.hallel.relational.api.app.user.repository;

import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<UserProfileResponse> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    UUID id(UUID id);

    Optional<User> findByToken(String token);
}
