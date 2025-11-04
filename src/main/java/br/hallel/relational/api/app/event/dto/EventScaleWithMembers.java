package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.enum_type.MemberEventScaleStatus;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
public class EventScaleWithMembers {
    private UUID scaleId;
    private Date date;
    private Event event;
    private MemberEventScaleStatus status;
    List<String> membersParticipate;
    List<String> membersDecline;
    List<String> membersInvited;

    public EventScaleWithMembers(UUID scaleId, Date date, Event event, MemberEventScaleStatus status) {
        this.scaleId = scaleId;
        this.date = date;
        this.event = event;
        this.status = status;
    }
}
