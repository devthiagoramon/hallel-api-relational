package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.Ministry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MinistryRepository extends JpaRepository<Ministry, UUID> {
}
