package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "music_ministry_id")
    private MusicMinistry musicMinistry;

    @ManyToOne
    @JoinColumn(name = "dance_ministry_id")
    private DanceMinistry danceMinistry;
}
