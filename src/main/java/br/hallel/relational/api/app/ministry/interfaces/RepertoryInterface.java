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

    RepertoryResponse addOrRemoveMusicsRepertory(UUID idRepertory, MusicRepertoryAddRemoveDTO musicDTO);

    RepertoryResponse addOrRemoveDanceRepertory(UUID idRepertory, DanceRepertoryAddRemoveDTO danceDTO);

    RepertoryResponse addOrRemovePlaylistRepertory(UUID idRepertory, PlaylistAddRemoveDTO playlistDTO)
            ;

    RepertoryResponse listRepertoryWithDancesAndMusic(UUID idRepertorio);

    List<MusicResponse> listMusicsByRepertoryId(UUID ministryId);

    List<DanceResponse> listDancesByRepertoryId(UUID ministryId);

    List<PlaylistResponse> listPlaylistsByRepertoryId(UUID ministryId);

    RepertoryMusicAndDanceResponse listMusicAndDanceByRepertoryId(UUID ministryId);

    MusicResponse editMusicRepertory(UUID idRepertory, UUID idMusic, MusicAddEditDTO musicRepertoryAddRemoveDTO);

    DanceResponse editDanceRepertory(UUID idRepertory, UUID idDance, DanceAddEditDTO danceRepertoryAddRemoveDTO);

}