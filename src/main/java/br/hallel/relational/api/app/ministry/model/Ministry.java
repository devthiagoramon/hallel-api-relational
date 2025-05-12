package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor @NoArgsConstructor

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
    private User coordinatorId;

    @ManyToOne
    @JoinColumn(name = "vice_coordinator_id")
    private User viceCoordinatorId;

    @ManyToMany
    @JoinTable(
            name = "scale_ministries",
            joinColumns = @JoinColumn(name = "ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<EventScale>  eventScalesList;

    @ManyToMany
    @JoinTable(
            name = "member_ministry",
            joinColumns = @JoinColumn(name = "ministry_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> membersMinistry;

}
