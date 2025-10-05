package br.hallel.relational.api.app.event.interfaces;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ScaleInterface {
    EventScaleResponse createScale(Event event, UUID idEvent);

    EventScaleResponse editDateScale(UUID id, Date date);

    EventScaleWithInfos getEventScaleWithInfos(UUID id);

    List<String> listMemberMinisteryCanInviteToScale(
            UUID id, int page, int size
    );

    EventScaleResponse editScaleInviteMemberMinistry(UUID id, List<UUID> idMemberMinistryList);

    EventScaleResponse toggleScaleConfirmedMemberMinistry(UUID id, List<UUID> idMemberMinistryList);

       List<ScaleEventWithEventInfoResponse> listScale();

    List<ScaleEventWithEventInfoResponse> listScaleMemberIdCanParticipate(
            UUID memberId, LocalDateTime start, LocalDateTime end);
    List<ScaleEventWithEventInfoResponse> listScaleMinistryIdsByMemberIdThatCanParticipate(UUID membroId, LocalDateTime start, LocalDateTime end);

    List<ScaleEventWithEventInfoResponse> listScaleMinistryConfirmedMember(UUID membroId, LocalDateTime start, LocalDateTime end);

    List<SimpleScaleResponse> listScaleMinistryIdsByMembroIdThatConfirmed(UUID membroId, LocalDateTime start, LocalDateTime end);

    List<ScaleEventWithEventInfoResponse> listScaleMinistryRangeDate(
            LocalDateTime start, LocalDateTime end);

    List<ScaleEventWithEventInfoResponse> listScaleMinistryIdsByMinistryIdAndRangeDate(UUID idMinisterio, LocalDateTime start, LocalDateTime end);

    List<ScaleEventWithEventInfoResponse> listScaleMinistryRangeDateByMinistryId(
            UUID idMinisterio, LocalDateTime start,
            LocalDateTime end);

    ScaleEventWithEventInfoResponse listScaleMinistryByIdWithInfos(
            UUID idEscalaMinisterio);


//    List<NotConfirmedScaleMinistry> listReasonsAbsenceMemberEventByIdScalesMinistry(
//            UUID idEscala);

    void deleteScaleWithDeletingEvent(UUID idEvento);

    EventScaleWithRepertoriesResponse addAndRemoveRepertoryInScale(
            UUID idEscalaMinisterio,
            ScaleRepertoryDTO escalaRepertorioDTO);

    EventScaleResponse getEventScaleById(UUID id);

    EventByEventScaleResponse getEventByEventScaleId(UUID id);
    List<MinistrySimpleResponse> listMinistriesByEventId(UUID eventId);
}

