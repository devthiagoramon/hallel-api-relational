package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventScale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberEventScaleResponseInfosScale {
    private UUID userId;
    private EventScale eventScale;
}
