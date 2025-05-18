package br.hallel.relational.api.app.event.interfaces;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.NotConfirmedScaleMinistry;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ScaleInterface {
    EventScaleResponse createScale(Event event, UUID idEvent);

    EventScaleResponse editDateScale(UUID id, Date date);

    List<String> listMemberMinisteryCanInviteToScale(
            UUID id, int page, int size
    );

    EventScaleResponse editScaleInviteMemberMinistry(UUID id, List<UUID> idMemberMinistryList);

    EventScaleResponse toggleScaleConfirmedMemberMinistry(UUID id, List<UUID> idMemberMinistryList);

    EventScaleResponse toggleScaleNOTConfirmedMemberMinistry(
            UUID id,
            List<NotConfirmedScaleDTO> notConfirmed
    );

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


    List<NotConfirmedScaleMinistry> listReasonsAbsenceMemberEventByIdScalesMinistry(
            UUID idEscala);

    void deleteSacleWithDeletingEvent(UUID idEvento);

    ScaleEventResponseWithInfos addAndRemoveRepertoryInScale(
            UUID idEscalaMinisterio,
            ScaleRepertoryDTO escalaRepertorioDTO);

    EventScaleResponse getEventScaleById(UUID id);
}

