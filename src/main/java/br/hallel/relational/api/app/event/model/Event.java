package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.event.model.enum_type.EventStatus;
import br.hallel.relational.api.app.event.model.enum_type.EventType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
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

    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;

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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    @JsonManagedReference
    private List<EventInviteBatch> eventInviteBatches = new ArrayList<>();

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

    @Column(name = "whatsapp_group_link", nullable = true)
    @JsonIgnore
    private String whatsAppGroupLink;

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

    public void addInviteBatch(EventInviteBatch inviteBatch) {
        if (eventInviteBatches == null) {
            eventInviteBatches = new ArrayList<>();
            eventInviteBatches.add(inviteBatch);
            inviteBatch.setEvent(this);
        }else {
            eventInviteBatches.add(inviteBatch);
            inviteBatch.setEvent(this);
        }
    }

    public void removeInviteBatch(EventInviteBatch inviteBatch) {
        if (eventInviteBatches != null) {
            eventInviteBatches.remove(inviteBatch);
            inviteBatch.setEvent(this);
        }
    }

}
