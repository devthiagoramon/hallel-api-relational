package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.NotConfirmedScaleMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotConfirmedScaleMinistryRepository extends JpaRepository<NotConfirmedScaleMinistry, UUID> {
    List<NotConfirmedScaleMinistry> findAllByMemberMinistry_Id(MemberMinistryId memberMinistryId);
    Optional<NotConfirmedScaleMinistry> findByMemberMinistry_IdAndEventScale_Id(MemberMinistryId memberMinistryId, UUID eventScaleId);
}
