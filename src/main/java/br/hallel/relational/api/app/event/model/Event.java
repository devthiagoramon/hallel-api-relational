package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.event.EventType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "events")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "local_event_name", nullable = false)
    private String local_event_name;

    @Column(name = "local_event_longitude", nullable = false)
    private Double local_event_longitude;

    @Column(name = "local_event_latitude", nullable = false)
    private Double local_event_latitude;

    @Column(name = "image_url", nullable = false)
    private String image_url;

    @Column(name = "banner_url", nullable = false)
    private String banner_url;

    @Column(name = "is_important", nullable = false)
    private Boolean isImportant = false;

    @Column(name = "value", nullable = false)
    private Double value = 0.0;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    @JsonBackReference
    @JsonIgnore
    private List<EventScale> scales;

    @Column(name = "has_ended", nullable = false)
    private Boolean hasEnded = false;

    @Column(name = "its_free", nullable = true)
    private Boolean itsFree = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = true)
    private EventType eventType;

    @ElementCollection
    @CollectionTable(
            name = "retreat_schedule",
            joinColumns = @JoinColumn(name = "retreat_id")
    )
    @Column(name = "activity")
    private List<String> schedule;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventTransaction> transactions;

}
