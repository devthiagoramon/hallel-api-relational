package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.ministry.model.RepertoryMinistry;
import lombok.*;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class EventScaleWithRepertoriesResponse {
    private UUID id;
    private UUID ministryId;
    private List<RepertoryMinistry> repertories;

    @PersistenceCreator
    public EventScaleWithRepertoriesResponse(UUID id, UUID ministryId, List<RepertoryMinistry> repertories) {
        this.id = id;
        this.ministryId = ministryId;
        this.repertories = repertories;
    }
}
