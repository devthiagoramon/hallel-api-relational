package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.ministry.model.Ministry;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "event_schedule")
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "date", nullable = false)
    private OffsetDateTime date;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "edited_at")
    private OffsetDateTime editedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;


}
