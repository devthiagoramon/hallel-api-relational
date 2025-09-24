package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.MessageScaleDeliveryStatus;
import br.hallel.relational.api.app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatusReadingMessageUserResponse {
    private User user;
    private MessageScaleDeliveryStatus status;
}
