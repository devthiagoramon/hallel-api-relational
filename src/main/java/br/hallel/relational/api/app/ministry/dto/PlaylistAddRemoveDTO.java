package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.MinistryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistAddRemoveDTO {

    private MinistryType ministryType;
    private List<UUID> addMusicMinistry;
    private List<UUID> removeMusicMinistry;
    private List<UUID> addDanceMinistry;
    private List<UUID> removeDanceMinistry;
}
