package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Table(name = "member_event_scale")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberEventScale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberEventScaleStatus status;

    @Column(name = "reason_absence")
    private String reason_absence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_scale_id")
    private EventScale eventScale;

    public MemberEventScale(MemberEventScaleStatus status,
                            String reason_absence, User user,
                            EventScale eventScale) {
        this.status = status;
        this.reason_absence = reason_absence;
        this.user = user;
        this.eventScale = eventScale;
    }
}
