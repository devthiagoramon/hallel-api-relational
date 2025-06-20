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
@AllArgsConstructor
@NoArgsConstructor
public class RepertoryResponse {

    private UUID id;
    private String name;
    private String description;
    private MinistryType ministryType;
    private UUID ministryId;
    private String linkPlaylist;
    private List<VideoMinistry> videoMinistryList;
    private List<MusicResponse> musicMinistryList;
    private List<DanceResponse> danceMinistryList;

}
