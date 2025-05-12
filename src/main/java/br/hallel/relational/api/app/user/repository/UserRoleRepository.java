package br.hallel.relational.api.app.user.repository;

import br.hallel.relational.api.app.user.model.UserRole;
import br.hallel.relational.api.app.user.model.UserRoleIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleIds> {
}
