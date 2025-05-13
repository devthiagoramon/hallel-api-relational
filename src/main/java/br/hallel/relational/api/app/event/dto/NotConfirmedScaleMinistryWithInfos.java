package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class NotConfirmedScaleMinistryWithInfos {
    private UUID id;
    private User membro;
    private UUID idEscalaMinisterio;
    private UUID reason;
}
