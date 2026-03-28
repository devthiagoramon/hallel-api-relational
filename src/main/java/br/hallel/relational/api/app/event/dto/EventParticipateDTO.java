package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventParticipationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipateDTO {
    private UUID eventId;
    private String community;
    private String formation;
    private EventParticipationType participationType;
    private String name;
    private String email;
    private String phoneNumber;
    private String cpf;
    private Boolean isMarried;
    private OffsetDateTime dateBirth;
    private UUID eventInviteId;

    // Campos opcionais para pagamento com cartão
    private String cardToken;
    private Integer installments;
    private String paymentMethodId;
}
