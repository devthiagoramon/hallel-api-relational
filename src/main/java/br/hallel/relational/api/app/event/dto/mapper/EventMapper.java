package br.hallel.relational.api.app.event.dto.mapper;

import br.hallel.relational.api.app.event.dto.EventDTO;
import br.hallel.relational.api.app.event.dto.EventResponse;
import br.hallel.relational.api.app.event.dto.EventResponseWithMinistryAssociated;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class EventMapper {


    @Mapping(target = "ministriesAssocied", ignore = true)
    @Mapping(target = "schedules", source = "eventSchedules")
    public abstract EventResponse entityToResponse(Event event);

    @AfterMapping
    protected void afterMapping(@MappingTarget EventResponse response, Event event) {
        if (event.getScales() != null) {
            List<MinistrySimpleResponse> ministries = event.getScales().stream()
                    .map(EventScale::getMinistry)
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(ministry -> new MinistrySimpleResponse(
                            ministry.getId(),
                            ministry.getTitle(),
                            ministry.getImage(), ministry.getMinistryType()))
                    .toList();

            response.setMinistriesAssocied(ministries);
        }
    }

    @Mapping(target = "scales", ignore = true)
    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "itsFree", ignore = true)
    @Mapping(target = "participations", ignore = true)
    @Mapping(target = "foods", ignore = true)
    @Mapping(target = "eventStatus", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "eventSchedules", source = "schedules")
    public abstract Event responseToEntity(EventResponse eventResponse);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "banner_url", ignore = true)
    @Mapping(target = "image_url", ignore = true)
    @Mapping(target = "scales", ignore = true)
    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "eventSchedules", ignore = true)
    @Mapping(target = "eventInvites", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "itsFree", ignore = true)
    @Mapping(target = "participations", ignore = true)
    @Mapping(target = "foods", ignore = true)
    @Mapping(target = "eventStatus", ignore = true)
    @Mapping(target = "duration", ignore = true)
    public abstract Event dtoToEntity(EventDTO event);

    @Mapping(target = "ministries", ignore = true)
    public abstract EventResponseWithMinistryAssociated eventToResponseWithMinistryAssociated(Event event);
}
