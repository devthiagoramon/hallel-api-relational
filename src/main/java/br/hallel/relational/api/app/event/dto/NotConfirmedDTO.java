package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class NotConfirmedDTO {
    private MemberMinistryId member;
    private UUID eventScale;
    private String reason;
}
