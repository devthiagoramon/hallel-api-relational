package br.hallel.relational.api.app.ministry.dto.mapper;

import br.hallel.relational.api.app.ministry.dto.*;
import br.hallel.relational.api.app.ministry.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RepertoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ministry", ignore = true)
    RepertoryResponse requestToResponse(RepertoryRequestDTO repertoryRequestDTO);

    @Mapping(target = "ministry", ignore = true)
    RepertoryRequestDTO responseToRequest(RepertoryResponse repertoryResponse);

    RepertoryResponse entityToResponse(RepertoryMinistry repertory);

    RepertoryMinistry responseToEntity(RepertoryResponse repertoryResponse);

    List<RepertoryResponse> toListResponseRepertory(List<RepertoryMinistry> repertories);



    DanceResponse danceEntityToResponse(DanceMinistry dance);
    DanceMinistry danceResponseToEntity(DanceResponse danceResponse);
    @Mapping(target = "coordinatorId", source = "coordinator.id")
    @Mapping(target = "viceCoordinatorId", source = "viceCoordinator.id")
    MinistryDTO_MusicResponse ministryToDto(Ministry ministry);

    @Mapping(target = "ministry", source = "ministry")
    MusicResponse musicEntityToResponse(MusicMinistry music);

    @Mapping(target = "ministry", ignore = true)
    MusicMinistry musicResponseToEntity(MusicResponse musicResponse);

    List<MusicResponse> toListMusicResponse(List<MusicMinistry> musicList);
    List<DanceResponse> toListDanceResponse(List<DanceMinistry> reponseList);
    List<PlaylistResponse> toListPlaylistResponse(List<PlaylistRepertory> reponseList);
}
