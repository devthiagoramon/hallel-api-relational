package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.DanceMinistry;
import br.hallel.relational.api.app.ministry.model.MinistryType;
import br.hallel.relational.api.app.ministry.model.MusicMinistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PlaylistResponse {
    private UUID id;
    private MinistryType ministryType;
    private List<MusicMinistry> musicMinistries;
    private List<DanceMinistry> danceMinistries;
}
