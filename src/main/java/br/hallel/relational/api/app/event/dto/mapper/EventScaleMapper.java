package br.hallel.relational.api.app.event.dto.mapper;

import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.event.model.EventScale;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EventScaleMapper {
    EventScaleResponse entityToResponse(EventScale eventScale);
}
