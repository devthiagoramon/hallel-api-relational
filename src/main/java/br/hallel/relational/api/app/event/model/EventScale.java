package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.ministry.model.AuditionMinistry;
import br.hallel.relational.api.app.ministry.model.Ministry;
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

    @ManyToOne
    @JoinColumn(name = "event_scale_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

    @OneToOne(mappedBy = "eventScale", cascade = CascadeType.ALL)
    private AuditionMinistry auditionMinistry;

    @Column(name = "date")
    private Date date;

    @Column(name = "is_audition")
    private boolean isAudition;

    @ElementCollection
    @CollectionTable(
            name = "event_scale_members_invited",
            joinColumns = @JoinColumn(name = "event_scale_id"))
    @Column(name = "members_id", nullable = false)
    private List<UUID> membersMinistryInvitedIds;

    @ElementCollection
    @CollectionTable(name = "event_scale_members_confirmed",
            joinColumns = @JoinColumn(name = "event_scale_id"))
    @Column(name = "members_id", nullable = false)
    private List<UUID> membersMinistryConfirmeds;

    @ElementCollection
    @CollectionTable(name = "event_scale_members_not_confirmed", joinColumns = @JoinColumn(name = "event_scale_id"))
    @Column(name = "members_id", nullable = false)
    private List<UUID> membersMinistryNotConfirmedIds;

    @ElementCollection
    @CollectionTable(name = "event_scale_repertories", joinColumns = @JoinColumn(name = "event_scale_id"))
    @Column(name = "repertories_id", nullable = false)
    private List<UUID> repertoryIds;
}
