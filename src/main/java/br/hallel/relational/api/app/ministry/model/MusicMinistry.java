package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "music_ministry")
@Entity
public class MusicMinistry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String letter;
    @Column
    private String link;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;
}
