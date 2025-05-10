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
    private UUID id;
    private String title;
    private String description;
    private String image;
    private Boolean hasRepertoire;
    private MinistryType ministryType;
    private User coordinatorId;
    private User viceCoordinatorId;
    private List<EventScale> eventScalesList;
}
