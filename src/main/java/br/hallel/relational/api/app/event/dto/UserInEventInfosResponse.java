package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInEventInfosResponse {
    private UUID participationId;
    private UUID eventId;
    private UUID userId;
    private String eventName;
    private String userName;
    private String userEmail;
    private String phoneNumber;
    private boolean isMarried;
    private OffsetDateTime dateBirth;
    private Double invitePrice;
    private UserFunctionInEvent userFunctionInEvent;
    private StatusPaymentEventParticipation statusPaymentEventParticipation;
    private String community;
    private int quantity;

    public UserInEventInfosResponse toResponse(EventParticipation dto, int quantity) {
        return new UserInEventInfosResponse(dto.getId(), dto.getEvent().getId(),
                dto.getUser() != null ? dto.getUser().getId() : null,
                dto.getEvent().getTitle(), dto.getName(), dto.getEmail(), dto.getPhoneNumber(), dto.getIsMarried(),
                dto.getDateBirth(), dto.getEventInviteAssociated().getValue(),
                dto.getUserFunctionInEvent(), dto.getStatusPaymentEventParticipation(), dto.getCommunity(), quantity);
    }

}
