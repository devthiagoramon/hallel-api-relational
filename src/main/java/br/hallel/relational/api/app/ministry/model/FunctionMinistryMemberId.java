package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionMinistryMemberId {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "function_ministry_id")
    private UUID functionMinistryId;


}
