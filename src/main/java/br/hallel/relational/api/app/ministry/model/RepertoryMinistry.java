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

@Table(name = "repertory_ministry")
@Entity
public class RepertoryMinistry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private MinistryType ministryType;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

    @ManyToMany
    @JoinTable(
            name = "playlist_ministries",
            joinColumns = @JoinColumn(name = "repertory_ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "playlist_id")
    )
    private List<PlaylistRepertory> playlistRepertoryList;

    @ManyToMany
    @JoinTable(
            name = "video_ministries",
            joinColumns = @JoinColumn(name = "repertory_ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private List<VideoMinistry> videoMinistryList;

    @ManyToMany
    @JoinTable(
            name = "music_ministries",
            joinColumns = @JoinColumn(name = "repertory_ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "ministry_id")
    )
    private List<MusicMinistry> musicMinistryList;

    @ManyToMany
    @JoinTable(
            name = "dance_ministries",
            joinColumns = @JoinColumn(name = "repertory_ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "dance_ministry_id")
    )
    private List<DanceMinistry> danceMinistryList;
}
