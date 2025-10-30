package br.hallel.relational.api.app.global.pdf.dto;

import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import br.hallel.relational.api.app.user.utils.UserUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventParticipationForPDF {

    String name;
    String email;
    Integer age;
    Double inviteValue;
    StatusPaymentEventParticipation status;
    UserFunctionInEvent userFunction;
    String community;

    public static List<EventParticipationForPDF> fromListParticipation(List<EventParticipation> eventParticipations) {
        List<EventParticipationForPDF> eventParticipationForPDFs = new ArrayList<>();
        for (EventParticipation eventParticipation : eventParticipations) {
            eventParticipationForPDFs.add(new EventParticipationForPDF(
                    eventParticipation.getName(),
                    eventParticipation.getEmail(),
                    UserUtils.getAge(Date.from(eventParticipation.getDateBirth().toInstant())),
                    eventParticipation.getEventInviteAssociated() != null ?
                            eventParticipation.getEventInviteAssociated().getValue() : null,
                    eventParticipation.getStatusPaymentEventParticipation() != null ?
                            eventParticipation.getStatusPaymentEventParticipation() : null,
                    eventParticipation.getUserFunctionInEvent(),
                    eventParticipation.getCommunity()
            ));
        }
        return eventParticipationForPDFs;
    }
}
