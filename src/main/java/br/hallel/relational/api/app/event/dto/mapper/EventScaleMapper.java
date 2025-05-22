package br.hallel.relational.api.app.event.dto.mapper;

import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.event.dto.ScaleEventResponseWithInfos;
import br.hallel.relational.api.app.event.model.EventScale;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EventScaleMapper {

    EventScaleResponse entityToResponse(EventScale eventScale);
//    @Mapping(target = "confirmedMembers", ignore = true)
//    @Mapping(target = "invitedMembers", ignore = true)
//    @Mapping(target = "notConfirmedMembers", ignore = true)
//    @Mapping(target = "repertories", ignore = true)
    @Mappings({
            @Mapping(target = "ministry", ignore = true),
    })
    EventScale responseToEntity(EventScaleResponse eventScaleResponse);

    @Mapping(source = "ministry.id", target = "ministryId")
    @Mapping(source = "event.id", target = "eventId")
//    @Mapping(source = "invitedMembers", target = "membersMinistryInvitedIds")
//    @Mapping(source = "confirmedMembers", target = "membersMinistryConfirmedIds")
//    @Mapping(source = "notConfirmedMembers", target = "membersMinistryNotConfirmedIds")
//    @Mapping(source = "repertories", target = "repertoryIds")
    @Mapping(target = "auditionMinistryId", ignore = true)
    @Mapping( target = "ensaio", ignore = true)
    ScaleEventResponseWithInfos entityToResponseWithInfos(EventScale eventScale);

    List<EventScaleResponse> listEntityToResponse(List<EventScale> eventScaleList);
}
