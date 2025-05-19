package br.hallel.relational.api.app.ministry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor @Setter @Getter
public class RepertoryMusicAndDanceResponse {
    private UUID id;
    private List<MusicResponse> musicMinistryList;
    private List<DanceResponse> danceMinistryList;

}
