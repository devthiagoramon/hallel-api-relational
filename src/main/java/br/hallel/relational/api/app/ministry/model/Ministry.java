package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;

import java.util.UUID;

@Table(name = "ministry")
@Entity
public class Ministry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String title;
    private String description;
    private String image;
    private Boolean hasRepertoire;
    private MinistryType ministryType;

    @ManyToOne
    @JoinColumn(name = "coordinator_id")
    private User coordinator_id;

    @ManyToOne
    @JoinColumn(name = "vice_coordinator_id")
    private User vice_coordinator_id;

}
