package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.Ministry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MinistryDTO_MusicResponse {
    private UUID id;
    private String title;
    private String image;
    private UUID coordinatorId;
    private UUID viceCoordinatorId;

    public MinistryDTO_MusicResponse(Ministry ministry) {
        this.id = ministry.getId();
        this.title = ministry.getTitle();
        this.image = ministry.getImage();
        this.coordinatorId = ministry.getCoordinator().getId();
        this.viceCoordinatorId = ministry.getViceCoordinator().getId();
    }
}
