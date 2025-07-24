package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.MinistryMemberRole;
import br.hallel.relational.api.app.ministry.model.MinistryMemberRoleIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinistryMemberRoleRepository extends JpaRepository<MinistryMemberRole, MinistryMemberRoleIds> {
}
