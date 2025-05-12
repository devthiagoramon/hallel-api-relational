package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.ministry.model.MinistryType;
import br.hallel.relational.api.app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinistryRequestDTO {
    private String title;
    private String description;
    private Boolean hasRepertoire;
    private MinistryType ministryType;
    private UUID coordinatorId;
    private UUID viceCoordinatorId;
}
