package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.MessageScaleDeliveryStatus;
import br.hallel.relational.api.app.ministry.model.MessageScaleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MessageReadSocketResponse {
    private UUID messageId;
    private MessageScaleDeliveryStatus status;
}
