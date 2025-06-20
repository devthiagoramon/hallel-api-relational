package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.RepertoryMusicMinistry;
import br.hallel.relational.api.app.ministry.model.RepertoryMusicMinistryIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RepertoryMusicMinistryRepository extends JpaRepository<RepertoryMusicMinistry, RepertoryMusicMinistryIds> {
    void deleteByMusicMinistry_Id(UUID musicMinistryId);
    void deleteAllByMusicMinistry_Id(UUID musicMinistryId);
}
