package br.hallel.relational.api.app.event.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "events")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "duration")
    private Duration duration;

    @Column(name = "event_status")
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    @JsonManagedReference
    private List<EventInvite> eventInvites = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    @JsonManagedReference
    private List<EventSchedule> eventSchedules = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    @ToString.Exclude
    @JsonBackReference
    @JsonIgnore
    private List<EventScale> scales;

    @Column(name = "its_free", nullable = true)
    private Boolean itsFree = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = true)
    private EventType eventType;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EventTransaction> transactions;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EventParticipation> participations;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Foods> foods;

    public void addInvite(EventInvite invite) {
        if (eventInvites == null) {
            eventInvites = new ArrayList<>();
            eventInvites.add(invite);
            invite.setEvent(this);
        } else {
            eventInvites.add(invite);
            invite.setEvent(this);
        }
    }

    public void addSchedule(EventSchedule schedule) {
        if (eventSchedules != null) {
            this.eventSchedules.add(schedule);
            schedule.setEvent(this);
        } else {
            this.eventSchedules = new ArrayList<>();
            this.eventSchedules.add(schedule);
            eventSchedules.add(schedule);
            schedule.setEvent(this);
        }
    }

}
