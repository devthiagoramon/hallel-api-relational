package br.hallel.relational.api.app.event.dto.mapper;

import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.event.dto.ScaleEventResponseWithInfos;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.NotConfirmedScaleMinistry;
import br.hallel.relational.api.app.ministry.model.RepertoryMinistry;
import br.hallel.relational.api.app.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.UUID;

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

}
