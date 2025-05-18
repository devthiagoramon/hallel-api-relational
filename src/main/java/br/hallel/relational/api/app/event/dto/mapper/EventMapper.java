package br.hallel.relational.api.app.event.dto.mapper;

import br.hallel.relational.api.app.event.dto.EventDTO;
import br.hallel.relational.api.app.event.dto.EventResponse;
import br.hallel.relational.api.app.event.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "value", ignore = true)
    @Mapping(target = "eventScalesList", ignore = true)
    @Mapping(target = "banner_url", ignore = true)
    @Mapping(target = "image_url", ignore = true)

    EventResponse dtoToResponse(EventDTO eventDTO);

    @Mapping(target = "eventScalesList", ignore = true)
    EventResponse entityToResponse(Event event);

    @Mapping(target = "eventScalesList", ignore = true)
    Event responseToEntity(EventResponse eventResponse);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "value", ignore = true)
    @Mapping(target = "eventScalesList", ignore = true)
    @Mapping(target = "banner_url", ignore = true)  // Ignora banner_url
    @Mapping(target = "image_url", ignore = true)   // Ignora image_url
    Event dtoToEntity(EventDTO event);
}
