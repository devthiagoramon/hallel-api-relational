package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@Access(AccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionMinistryId {

    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, name = "ministry_id")
    private UUID ministryId;

    public FunctionMinistryId(UUID ministryId) {
        this.ministryId = ministryId;
    }
}
