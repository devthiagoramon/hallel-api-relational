package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.GuestInvitedEventScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GuestInvitedEventScaleRepository extends
        JpaRepository<GuestInvitedEventScale, UUID> {
    List<GuestInvitedEventScale> findAllByEventScale_Id(UUID eventScaleId);

    UUID id(UUID id);
}