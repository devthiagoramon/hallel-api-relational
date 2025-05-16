package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.ministry.model.AuditionMinistry;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.model.RepertoryMinistry;
import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "event_scale")
@Entity
@Data
public class EventScale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "scale_ministry_confirmed",
            joinColumns = @JoinColumn(name = "scale_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> confirmedMembers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "scale_ministry_invited_members",
            joinColumns = @JoinColumn(name = "event_scale_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> invitedMembers;

    @OneToMany(mappedBy = "eventScale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "not_confirmed_members")
    private List<NotConfirmedScaleMinistry> notConfirmedMembers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "event_scale_repertory",
            joinColumns = @JoinColumn(name = "event_scale_id"),
            inverseJoinColumns = @JoinColumn(name = "repertory_ministry_id")
    )
    private List<RepertoryMinistry> repertories;
}
