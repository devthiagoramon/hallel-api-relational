package br.hallel.relational.api.app.ministry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class DanceRepertoryAddRemoveDTO {
    private List<UUID> danceIdsAdd;
    private List<UUID> danceIdsRemove;
}
