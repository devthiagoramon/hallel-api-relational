package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class ScaleRepertoryDTO {
    private List<UUID> repertoryIdsAdd;
    private List<UUID> repertoryIdsRemove;
}
