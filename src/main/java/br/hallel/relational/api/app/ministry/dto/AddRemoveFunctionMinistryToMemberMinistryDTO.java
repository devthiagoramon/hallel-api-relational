package br.hallel.relational.api.app.ministry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddRemoveFunctionMinistryToMemberMinistryDTO {
    private UUID functionMinistryId;
    private UUID memberMinistryId;
}
