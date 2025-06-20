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


    @Mapping(source = "ministry.id", target = "ministryId")
    RepertoryResponse entityToResponse(RepertoryMinistry repertory);

    @Mapping(source = "ministryId", target = "ministry.id")
    RepertoryMinistry responseToEntity(RepertoryResponse repertoryResponse);

    List<RepertoryResponse> toListResponseRepertory(List<RepertoryMinistry> repertories);
    @Mapping(target = "ministryId", source = "ministry.id")
    DanceResponse danceEntityToResponse(DanceMinistry dance);

    @Mapping(target = "ministry.id", source = "ministryId")
    DanceMinistry danceResponseToEntity(DanceResponse danceResponse);


    @Mapping(target = "ministryId", source = "ministry.id")
    MusicResponse musicEntityToResponse(MusicMinistry music);

    @Mapping(target = "ministry", ignore = true)
    MusicMinistry musicResponseToEntity(MusicResponse musicResponse);

    List<MusicResponse> toListMusicResponse(List<MusicMinistry> musicList);

    List<DanceResponse> toListDanceResponse(List<DanceMinistry> reponseList);



    @Mapping(target = "coordinator.id", source = "coordinatorId")
    @Mapping(target = "viceCoordinator.id", source = "viceCoordinatorId")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "hasRepertoire", ignore = true)
    @Mapping(target = "ministryType", ignore = true)
    @Mapping(target = "eventScalesList", ignore = true)
    @Mapping(target = "membersMinistry", ignore = true)
    Ministry ministryDtoToEntity(MinistryDTOResponse dto);

    @Mapping(target = "coordinatorId", source = "coordinator.id")
    @Mapping(target = "viceCoordinatorId", source = "viceCoordinator.id")
    MinistryDTOResponse ministryToDto(Ministry ministry);

}
