package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "not_confirmed_scale_ministry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotConfirmedScaleMinistry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "ministry_id", referencedColumnName = "ministry_id")
    })
    private MemberMinistry memberMinistry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_scale_id", nullable = false)
    private EventScale eventScale;

    @Column(name = "reason")
    private String reason;
    public NotConfirmedScaleMinistry(MemberMinistry memberMinistry, EventScale eventScale, String reason) {
        this.memberMinistry = memberMinistry;
        this.eventScale = eventScale;
        this.reason = reason;
    }


}