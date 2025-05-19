package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "playlist_repertory")
@Entity
public class PlaylistRepertory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column
    private MinistryType ministryType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "playlist_repertory_music",
            joinColumns = @JoinColumn(name = "playlist_repertory_id"),
            inverseJoinColumns = @JoinColumn(name = "music_ministry_id")
    )
    private List<MusicMinistry> musicMinistries;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "playlist_repertory_dance",
            joinColumns = @JoinColumn(name = "playlist_repertory_id"),
            inverseJoinColumns = @JoinColumn(name = "dance_ministry_id")
    )
    private List<DanceMinistry> danceMinistries;

}
