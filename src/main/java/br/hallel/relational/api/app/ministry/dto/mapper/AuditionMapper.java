package br.hallel.relational.api.app.ministry.dto.mapper;

import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.ministry.dto.AuditionDTO;
import br.hallel.relational.api.app.ministry.dto.AuditionResponse;
import br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse;
import br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse;
import br.hallel.relational.api.app.ministry.model.AuditionMinistry;
import br.hallel.relational.api.app.ministry.model.Ministry;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuditionMapper {

    @Mappings({
            @Mapping(target = "ministry", ignore = true),
            @Mapping(target = "eventScale", ignore = true),
            @Mapping(target = "id", ignore = true)
    })
    AuditionMinistry requestToEntity(AuditionDTO auditionDTO);

    @Mapping(target = "ministry", qualifiedByName = "toMinistryResponse")
    @Mapping(target = "eventScale", qualifiedByName = "toEventScaleResponse")
    AuditionResponse entityToResponse(AuditionMinistry audition);

    @Mapping(target = "ministry", qualifiedByName = "toMinistryEntity")
    @Mapping(target = "eventScale", qualifiedByName = "toEventScaleEntity")
    AuditionMinistry responseToEntity(AuditionResponse auditionResponse);

    List<AuditionResponse> toListResponse(List<AuditionMinistry> auditions);

    @Named("toMinistryResponse")
    default MinistrySimpleResponse toMinistryResponse(Ministry ministry) {
        if (ministry == null) return null;
        return new MinistrySimpleResponse(ministry.getId(), ministry.getTitle(), ministry.getImage(), ministry.getMinistryType());
    }

    @Named("toEventScaleResponse")
    default EventScaleSimpleResponse toEventScaleResponse(EventScale eventScale) {
        if (eventScale == null) return null;
        return new EventScaleSimpleResponse(eventScale.getId(), eventScale.getDate());
    }

    @Named("toMinistryEntity")
    default Ministry toMinistryEntity(MinistrySimpleResponse ministrySimpleResponse) {
        if (ministrySimpleResponse == null) return null;
        Ministry ministry = new Ministry();
        ministry.setId(ministrySimpleResponse.id());
        ministry.setTitle(ministrySimpleResponse.title());
        ministry.setImage(ministrySimpleResponse.image());
        // Outros campos podem ser deixados nulos ou tratados conforme necessário
        return ministry;
    }

    @Named("toEventScaleEntity")
    default EventScale toEventScaleEntity(EventScaleSimpleResponse eventScaleSimpleResponse) {
        if (eventScaleSimpleResponse == null) return null;
        EventScale eventScale = new EventScale();
        eventScale.setId(eventScaleSimpleResponse.id());
        eventScale.setDate(eventScaleSimpleResponse.date());
        return eventScale;
    }
}
