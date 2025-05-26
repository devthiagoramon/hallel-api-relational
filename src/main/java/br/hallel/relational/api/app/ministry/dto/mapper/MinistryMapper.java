package br.hallel.relational.api.app.ministry.dto.mapper;


import br.hallel.relational.api.app.ministry.dto.MinistryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.model.Ministry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MinistryMapper {


    MinistryResponse entityMinistryToResponse(Ministry ministry);

    List<MinistryResponse> entityMinistriesToResponse(List<Ministry> ministries);

    @Mapping(target = "membersMinistry", ignore = true)
    @Mapping(target = "eventScalesList", ignore = true)
    Ministry responseToEntity(MinistryResponse ministryResponse);


    @Mapping(target = "coordinator", ignore = true)
    @Mapping(target = "viceCoordinator", ignore = true)
    @Mapping(target = "eventScalesList", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membersMinistry", ignore = true)
    @Mapping(target = "image", ignore = true)
    Ministry requestToEntity(MinistryRequestDTO ministryRequestDTO);

}
