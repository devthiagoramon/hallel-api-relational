package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class RepertoryRequestDTO {
    private String name;
    private String description;
    private MinistryType ministryType;
    private UUID ministryId;
    private List<UUID> playlistRepertoryIds;
    private List<UUID> videoMinistryIds;
    private List<UUID> musicMinistryIds;
    private List<UUID> danceMinistryIds;

}
