package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.EventInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventInviteRepository extends JpaRepository<EventInvite, UUID> {
}
