package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.ScaleChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScaleChatMessageRepository extends JpaRepository<ScaleChatMessage, UUID> {
}
