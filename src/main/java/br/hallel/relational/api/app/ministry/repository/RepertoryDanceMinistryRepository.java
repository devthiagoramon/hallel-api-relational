package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.RepertoryDanceMinistry;
import br.hallel.relational.api.app.ministry.model.RepertoryDanceMinistryIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepertoryDanceMinistryRepository extends JpaRepository<RepertoryDanceMinistry, RepertoryDanceMinistryIds> {
}
