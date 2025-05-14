package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.dto.mapper.EventScaleMapper;
import br.hallel.relational.api.app.event.interfaces.ScaleInterface;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ScaleService implements ScaleInterface {
    @Autowired
    private MemberMinistryRepository memberMinistryRepository;
    @Autowired
    private EventScaleRepository eventScaleRepository;
    @Autowired
    private MinistryService ministryService;

    private final MinistryMapper ministryMapper;
    private final EventScaleMapper scaleMapper;

    public ScaleService(MinistryMapper ministryMapper, EventScaleMapper scaleMapper) {
        this.ministryMapper = ministryMapper;
        this.scaleMapper = scaleMapper;
    }

    @Override
    public EventScaleResponse createScale(Event event, UUID idMinistry) {
        log.info("Creating escala for ministerio " + idMinistry + "...");
        EventScale eventScale = new EventScale();

        Ministry ministry = ministryMapper.responseToEntity(ministryService.getMinistryById(idMinistry));
        eventScale.setMinistry(ministry);
        eventScale.setDate(event.getDate());
        eventScale.setMembersMinistryInvitedIds(new ArrayList<>());
        eventScale.getMembersMinistryInvitedIds()
                .addAll(List.of(
                        ministry.getCoordinatorId().getId(),
                        ministry.getViceCoordinatorId().getId()
                ));
        eventScale.setMembersMinistryConfirmeds(new ArrayList<>());
        eventScale.setMembersMinistryNotConfirmedIds(new ArrayList<>());

        EventScale eventScaleSaved = this.eventScaleRepository.save(eventScale);
        log.info("Escala " + eventScaleSaved.getId() + " created for event " + event.getTitle() + " to ministerio " + idMinistry);
        return scaleMapper.entityToResponse(eventScaleSaved);
    }

    @Override
    public EventScaleResponse editDateScale(UUID id, Date date) {
        log.info("Editing escala " + id + "...");
        Optional<EventScale> eventScale = this.eventScaleRepository.findById(id);
        if (eventScale.isEmpty()) {
            throw new RuntimeException("Can't find escala with id " + id);
        }
        EventScale oldEventScale = eventScale.get();
        oldEventScale.setDate(date);
        EventScale scaleUpdated = this.eventScaleRepository.save(oldEventScale);

        return scaleMapper.entityToResponse(scaleUpdated);
    }

    @Override
    public List<String> listMemberMinisteryCanInviteToScale(UUID id, int page, int size) {

        return null;
    }

    @Override
    public EventScaleResponse editScaleInviteMemberMinistry(UUID id, List<UUID> idMemberMinistryList) {
        return null;
    }

    @Override
    public EventScaleResponse toggleScaleConfirmedMemberMinistry(UUID id, List<UUID> idMemberMinistryList) {
        return null;
    }

    @Override
    public EventScaleResponse toggleScaleNOTConfirmedMemberMinistry(UUID id, List<NotConfirmedScaleDTO> notConfirmed) {
        return null;
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScale() {
        log.info("Listing all the escala ministerio...");
        return this.eventScaleRepository.findAllWithEventosInfos();
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMemberIdCanParticipate(UUID memberId, LocalDateTime start, LocalDateTime end) {
        return this.eventScaleRepository.findAllByMemberIdAndDateBetween(memberId, start, end);
    }

    @Override
    public List<SimpleScaleResponse> listScaleMinistryIdsByMemberIdThatCanParticipate(UUID membroId, LocalDateTime start, LocalDateTime end) {
        return this.eventScaleRepository.findEscalaMinisterioIdsByMembroIdCanPaticipate(membroId, start, end);
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMinistryConfirmedMember(UUID membroId, LocalDateTime start, LocalDateTime end) {
        return this.eventScaleRepository.findConfirmedScalesByMemberAndDateRange(membroId, start, end);
    }

    @Override
    public List<SimpleScaleResponse> listScaleMinistryIdsByMembroIdThatConfirmed(UUID membroId, LocalDateTime start, LocalDateTime end) {
        return this.eventScaleRepository.findScalesConfirmedByMemberIdAndDateRange(membroId, start, end);
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMinistryRangeDate(LocalDateTime start, LocalDateTime end) {
        return this.eventScaleRepository.findScalesInRangeDate(start, end);
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMinistryIdsByMinistryIdAndRangeDate(UUID idMinisterio, LocalDateTime start, LocalDateTime end) {
        return this.eventScaleRepository.findScalesByMinistryIdAndDateRange(idMinisterio, start, end);
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMinistryRangeDateByMinistryId(UUID idMinisterio, LocalDateTime start, LocalDateTime end) {
        return this.eventScaleRepository.findScalesByMinistryIdAndDateRange(idMinisterio, start, end);
    }

    @Override
    public ScaleEventWithEventInfoResponse listScaleMinistryByIdWithInfos(UUID idEscalaMinisterio) {
        return this.eventScaleRepository.findScaleByIdWithInfos(idEscalaMinisterio);
    }

    @Override
    public List<NotConfirmedScaleMinistryWithInfos> listReasonsAbsenceMemberEventByIdScalesMinistry(UUID idEscala) {
        return this.eventScaleRepository.findReasonsAbsenceByEscalaId(idEscala);
    }

    @Override
    public void deleteSacleWithDeletingEvent(UUID idEvento) {

    }

    @Override
    public ScaleEventResponseWithInfos addAndRemoveRepertoryInScala(String idEscalaMinisterio, ScaleRepertoryDTO escalaRepertorioDTO) {
        return null;
    }
}
