package br.hallel.relational.api.app.security.repository;

import br.hallel.relational.api.app.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByDescription(String description);

    List<Role> findByDescriptionIn(Collection<String> descriptions);
}
