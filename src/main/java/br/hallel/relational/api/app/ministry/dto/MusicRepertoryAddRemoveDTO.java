package br.hallel.relational.api.app.ministry.dto;

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
public class MusicRepertoryAddRemoveDTO {

    private List<UUID> musicIdsAdd;
    private List<UUID> musicIdsRemove;

}
