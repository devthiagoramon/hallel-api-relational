package br.hallel.relational.api.app.ministry.repository;

import br.hallel.relational.api.app.ministry.model.EventScaleRepertory;
import br.hallel.relational.api.app.ministry.model.EventScaleRepertoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventScaleRepertoryRepository extends JpaRepository<EventScaleRepertory, EventScaleRepertoryId> {
}
