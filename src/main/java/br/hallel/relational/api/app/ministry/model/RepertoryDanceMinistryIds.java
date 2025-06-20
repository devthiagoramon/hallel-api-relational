package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RepertoryDanceMinistryIds {
    @Column(name = "repertory_ministry_id")
    private UUID repertoryMinistryId;

    @Column(name = "dance_ministry_id")
    private UUID danceMinistryId;
}
