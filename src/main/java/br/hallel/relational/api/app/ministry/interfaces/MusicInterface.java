package br.hallel.relational.api.app.ministry.interfaces;

import br.hallel.relational.api.app.ministry.dto.MusicAddEditDTO;
import br.hallel.relational.api.app.ministry.dto.MusicResponse;

import java.util.List;
import java.util.UUID;

public interface MusicInterface {
    MusicResponse createMusic(MusicAddEditDTO musicDTO);

    List<MusicResponse> listAllMusics();

    MusicResponse getMusicById(UUID id);

    void deleteMusicById(UUID id);
}
