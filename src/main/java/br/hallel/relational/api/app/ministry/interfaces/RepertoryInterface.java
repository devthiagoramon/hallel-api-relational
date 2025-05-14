package br.hallel.relational.api.app.ministry.interfaces;

import br.hallel.relational.api.app.ministry.dto.*;

import java.util.List;
import java.util.UUID;

public interface RepertoryInterface {
    RepertoryResponse createRepertory(RepertoryRequestDTO requestDTO);

    List<RepertoryResponse> listRepertoryByMinistryId(UUID id);

    RepertoryResponse getRepertoryById(UUID id);

    List<RepertoryResponse> listAllRepertory();

    RepertoryResponse editRepertory(UUID id, RepertoryRequestDTO requestDTO);

    void deleteRepertory(UUID id);

    RepertoryResponse addOrRemoveMusicsRepertory(UUID idRepertorio, MusicRepertoryAddRemoveDTO repertorioMusicDTO);

    RepertoryResponse addOrRemoveDanceRepertory(UUID idRepertorio, DanceRepertoryAddRemoveDTO repertorioDancaDTO);

    RepertoryResponse listRepertoryWithDancesAndMusic(UUID idRepertorio);

    List<MusicReponse> listMusicsByRepertoryId(UUID ministryId);

    List<DanceResponse> listDancesByRepertoryId(UUID ministryId);

    MusicReponse editMusicRepertory(UUID id, MusicEditDTO musicRepertoryAddRemoveDTO);

    DanceResponse editDanceRepertory(UUID id, DanceEditDTO danceRepertoryAddRemoveDTO);
}