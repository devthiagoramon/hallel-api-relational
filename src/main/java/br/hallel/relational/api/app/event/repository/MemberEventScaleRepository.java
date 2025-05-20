package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberEventScaleRepository extends JpaRepository<MemberEventScale, UUID> {
    List<MemberEventScale> findAllByStatusAndEventScale_Id(MemberEventScaleStatus status
            , UUID eventScaleId);

    MemberEventScale findByStatusAndEventScale_IdAndUser_Id(MemberEventScaleStatus status
            , UUID eventScaleId, UUID userId);
}
