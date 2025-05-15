package br.hallel.relational.api.app.ministry.interfaces;

import br.hallel.relational.api.app.ministry.dto.DanceAddEditDTO;
import br.hallel.relational.api.app.ministry.dto.DanceResponse;

import java.util.List;
import java.util.UUID;

public interface DanceInterface {
    DanceResponse createDance(DanceAddEditDTO danceDTO);

    List<DanceResponse> listAllDances();

    DanceResponse getDanceById(UUID id);

    void deleteDanceById(UUID id);
}
