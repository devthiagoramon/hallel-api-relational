package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class EventScaleRepertoryId {
    @Column(name = "event_scale_id")
    private UUID eventScaleId;
    @Column(name = "repertory_ministry_id")
    private UUID repertoryId;
}
