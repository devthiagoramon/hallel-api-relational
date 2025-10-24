package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.AgeGroup;
import br.hallel.relational.api.app.event.model.LimitEventAgeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LimitEventAgeGroupRepository extends
        JpaRepository<LimitEventAgeGroup, UUID> {
    Optional<LimitEventAgeGroup> findByEventIdAndAgeGroup(UUID eventId, AgeGroup ageGroup);

}
