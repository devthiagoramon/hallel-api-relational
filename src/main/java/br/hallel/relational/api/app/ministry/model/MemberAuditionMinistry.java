package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "member_audition_ministry")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberAuditionMinistry {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "audition_ministry_id")
    private AuditionMinistry auditionMinistry;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberEventScaleStatus status;

    public MemberAuditionMinistry(MemberEventScaleStatus status, User user, AuditionMinistry auditionMinistry) {
        this.status = status;
        this.user = user;
        this.auditionMinistry = auditionMinistry;
    }
}
