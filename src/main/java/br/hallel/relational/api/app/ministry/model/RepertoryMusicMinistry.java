package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "repertory_music_ministry")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RepertoryMusicMinistry {

    @EmbeddedId
    private RepertoryMusicMinistryIds repertoryMusicMinistryIds;

    public RepertoryMusicMinistry(RepertoryMusicMinistryIds repertoryMusicMinistryIds) {
        this.repertoryMusicMinistryIds = repertoryMusicMinistryIds;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repertory_ministry_id", insertable = false, updatable = false)
    private RepertoryMinistry repertoryMinistry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_ministry_id", insertable = false, updatable = false)
    private MusicMinistry musicMinistry;
}
