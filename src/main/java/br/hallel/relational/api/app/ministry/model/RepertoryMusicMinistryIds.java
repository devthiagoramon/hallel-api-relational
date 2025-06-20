package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RepertoryMusicMinistryIds {

    @Column(name = "repertory_ministry_id")
    private UUID repertoryMinistryId;

    @Column(name = "music_ministry_id")
    private UUID musicMinistryId;
}
