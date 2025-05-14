package br.hallel.relational.api.app.ministry.dto.mapper;

import br.hallel.relational.api.app.ministry.dto.DanceResponse;
import br.hallel.relational.api.app.ministry.dto.MusicReponse;
import br.hallel.relational.api.app.ministry.dto.RepertoryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.RepertoryResponse;
import br.hallel.relational.api.app.ministry.model.DanceMinistry;
import br.hallel.relational.api.app.ministry.model.MusicMinistry;
import br.hallel.relational.api.app.ministry.model.RepertoryMinistry;
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
    RepertoryResponse requestToResponse(RepertoryRequestDTO repertoryRequestDTO);

    RepertoryRequestDTO responseToRequest(RepertoryResponse repertoryResponse);

    RepertoryResponse entityToResponse(RepertoryMinistry repertory);

    RepertoryMinistry responseToEntity(RepertoryResponse repertoryResponse);

    List<RepertoryResponse> toListResponseRepertory(List<RepertoryMinistry> repertories);

    MusicReponse musicEntityToResponse(MusicMinistry music);
    DanceResponse danceEntityToResponse(DanceMinistry dance);
    List<MusicReponse> toListMusicResponse(List<MusicMinistry> reponseList);
    List<DanceResponse> toListDanceResponse(List<DanceMinistry> reponseList);
}
