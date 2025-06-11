package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.InviteEventScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InviteEventScaleRepository extends JpaRepository<InviteEventScale, UUID> {
}