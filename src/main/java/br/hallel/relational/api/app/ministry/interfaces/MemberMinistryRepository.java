package br.hallel.relational.api.app.ministry.interfaces;

import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberMinistryRepository extends JpaRepository<MemberMinistry, MemberMinistryId> {
}
