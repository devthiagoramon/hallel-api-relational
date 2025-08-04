package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "scale_chat_participant")
@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScaleChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_scale_id", unique = true)
    private EventScale eventScale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_scale_chat_id", unique = true)
    private MemberEventScale memberEventScale;

    public ScaleChatParticipant(EventScale eventScale, MemberEventScale memberEventScale) {
        this.eventScale = eventScale;
        this.memberEventScale = memberEventScale;
    }
}
