package br.hallel.relational.api.app.ministry.dto.mapper;

import br.hallel.relational.api.app.ministry.dto.FunctionMinistryDTO;
import br.hallel.relational.api.app.ministry.dto.FunctionMinistryResponse;
import br.hallel.relational.api.app.ministry.model.FunctionMinistry;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface FunctionMinistryMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    FunctionMinistry dtoToModel(FunctionMinistryDTO dto);

    @Mappings({
            @Mapping(target = "id", source = "id.id"),
            @Mapping(target = "ministryId", source = "id.ministryId")
    })
    FunctionMinistryResponse modelToResponse(FunctionMinistry model);


    @Mappings({
            @Mapping(target = "id", source = "id.id"),
            @Mapping(target = "ministryId", source = "id.ministryId")
    })
    List<FunctionMinistryResponse> listModelToResponseModel(List<FunctionMinistryResponse> functionMinistryResponses);
}
