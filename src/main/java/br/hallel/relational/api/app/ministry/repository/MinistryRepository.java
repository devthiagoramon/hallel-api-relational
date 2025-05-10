package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.Ministry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MinistryRepository extends JpaRepository<Ministry, UUID> {
}
