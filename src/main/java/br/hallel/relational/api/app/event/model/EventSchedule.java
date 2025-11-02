package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.event.model.enum_type.EventScheduleType;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
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

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventScheduleType type;


    @OneToMany(mappedBy = "eventSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventScheduleMinistry> ministries = new HashSet<>();

    @OneToMany(mappedBy = "eventSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventScheduleUser> users = new HashSet<>();

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "eventSchedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<EventScheduleVisitor> visitors = new HashSet<>();

    public void addVisitor(String email, String name, String phone, OffsetDateTime dob) {

        EventScheduleVisitor visitor = new EventScheduleVisitor();


        visitor.setEventSchedule(this);
        visitor.setName(name);
        visitor.setPhoneNumber(phone);
        visitor.setDateBirth(dob);

        visitor.setId(new EventScheduleVisitorIds(this.id, email));


        this.visitors.add(visitor);
    }

    public void addMinistry(Ministry ministry) {
        EventScheduleMinistry scheduleMinistry = new EventScheduleMinistry();

        scheduleMinistry.setEventSchedule(this);
        scheduleMinistry.setMinistry(ministry);

        scheduleMinistry.setId(new EventScheduleMinistryIds(this.id, ministry.getId()));

        this.ministries.add(scheduleMinistry);
    }


    public void addUser(User user) {
        EventScheduleUser scheduleUser = new EventScheduleUser();

        scheduleUser.setEventSchedule(this);
        scheduleUser.setUser(user);

        scheduleUser.setId(new EventScheduleUserIds(this.id, user.getId()));

        this.users.add(scheduleUser);
    }


}
