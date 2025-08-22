package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInEventInfosResponse {
    private UUID participationId;
    private UUID eventId;
    private UUID userId;
    private String userName;
    private String eventName;
    private String userEmail;
    private UserFunctionInEvent userFunctionInEvent;
    private StatusPaymentEventParticipation statusPaymentEventParticipation;
    private int quantity;
    public UserInEventInfosResponse toResponse(EventParticipation dto, int quantity) {
        return new UserInEventInfosResponse(dto.getId(),dto.getEvent().getId(), dto.getUser().getId(),
                dto.getUser().getName(), dto.getEvent().getTitle(), dto.getUser().getEmail(),
                dto.getUserFunctionInEvent(), dto.getStatusPaymentEventParticipation(), quantity);
    }

}
