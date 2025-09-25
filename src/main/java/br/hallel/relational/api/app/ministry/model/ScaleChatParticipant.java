package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"eventScale", "memberEventScale"})
public class ScaleChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_scale_id", unique = true)
    @JsonIgnore
    private EventScale eventScale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_scale_chat_id", unique = true)
    @JsonIgnore
    private MemberEventScale memberEventScale;

    public ScaleChatParticipant(EventScale eventScale, MemberEventScale memberEventScale) {
        this.eventScale = eventScale;
        this.memberEventScale = memberEventScale;
    }
}
