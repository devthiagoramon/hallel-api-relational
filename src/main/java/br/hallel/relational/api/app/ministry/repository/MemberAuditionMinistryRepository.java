package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.ministry.model.MemberAuditionMinistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberAuditionMinistryRepository extends JpaRepository<MemberAuditionMinistry, UUID> {
    Optional<MemberAuditionMinistry> findStatusByAuditionMinistry_IdAndUser_Id(UUID auditionId
    , UUID userId);
}
