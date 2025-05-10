package br.hallel.relational.api.app.ministry.dto.mapper;


import br.hallel.relational.api.app.ministry.dto.MinistryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.model.Ministry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MinistryMapper {
    MinistryResponse requestToMinistryResponse(MinistryRequestDTO ministryRequestDTO);


    MinistryResponse entityMinistryToResponse(Ministry ministry);

    Ministry responseToEntity(MinistryResponse ministryResponse);


    Ministry requestToEntity(MinistryRequestDTO ministryRequestDTO);

}
