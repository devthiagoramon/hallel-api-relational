package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repertory_dance_ministry")
public class RepertoryDanceMinistry {
    @EmbeddedId
    private RepertoryDanceMinistryIds ids;

    public RepertoryDanceMinistry(RepertoryDanceMinistryIds ids) {
        this.ids = ids;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repertory_ministry_id", insertable = false, updatable = false)
    private RepertoryMinistry repertoryMinistry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dance_ministry_id", insertable = false, updatable = false)
    private DanceMinistry danceMinistry;
}
