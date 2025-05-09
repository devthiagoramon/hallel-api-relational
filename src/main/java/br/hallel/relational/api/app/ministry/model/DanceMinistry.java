package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor

@Entity(name = "dance_ministry")
@Table
public class DanceMinistry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String link;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;
}
