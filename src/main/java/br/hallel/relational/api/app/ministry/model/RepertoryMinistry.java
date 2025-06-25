package br.hallel.relational.api.app.ministry.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "ministry_type", nullable = false)
    private MinistryType ministryType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ministry_id")
    @JsonManagedReference
    private Ministry ministry;

    @Column(name = "link_playlist")
    private String linkPlaylist;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "repertory_video_ministry",
            joinColumns = @JoinColumn(name = "repertory_ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "video_ministry_id")
    )
    private List<VideoMinistry> videoMinistryList;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "repertory_music_ministry",
            joinColumns = @JoinColumn(name = "repertory_ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "music_ministry_id")
    )
    private List<MusicMinistry> musicMinistryList;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "repertory_dance_ministry",
            joinColumns = @JoinColumn(name = "repertory_ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "dance_ministry_id")
    )
    private List<DanceMinistry> danceMinistryList;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepertoryMinistry that = (RepertoryMinistry) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
