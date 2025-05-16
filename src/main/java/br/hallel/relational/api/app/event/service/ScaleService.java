package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.dto.mapper.EventScaleMapper;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.exception.ListEventScaleIsEmpty;
import br.hallel.relational.api.app.event.interfaces.ScaleInterface;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.NotConfirmedScaleMinistry;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.model.RepertoryMinistry;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.RepertoryRepository;
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
    @Autowired
    private NotConfirmedScaleService notConfirmedService;
    @Autowired
    private RepertoryRepository repertoryMinistryRepository;
    private final MinistryMapper ministryMapper;
    private final EventScaleMapper scaleMapper;

    public ScaleService(MinistryMapper ministryMapper, EventScaleMapper eventScaleMapper) {
        this.ministryMapper = ministryMapper;
        this.scaleMapper = eventScaleMapper;
    }

    @Override
    public EventScaleResponse createScale(Event event, UUID idMinistry) {
        log.info("Creating escala for ministerio " + idMinistry + "...");
        EventScale eventScale = new EventScale();

        Ministry ministry = ministryMapper.responseToEntity(ministryService.getMinistryById(idMinistry));
        eventScale.setMinistry(ministry);
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

        List<ScaleEventWithEventInfoResponse> response = this.eventScaleRepository.findAllWithEventsInfosCanParticipateByMembroId(memberId, start, end);
        if (response.isEmpty()) {
            throw new ListEventScaleIsEmpty("List is empty!");
        }
        return response;
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMinistryIdsByMemberIdThatCanParticipate(UUID membroId, LocalDateTime start, LocalDateTime end) {

        List<ScaleEventWithEventInfoResponse> response = this.eventScaleRepository.
                findScaleEventsWithInfoByMemberIdCanParticipate(membroId, start, end);
        if (response.isEmpty()) {
            throw new ListEventScaleIsEmpty("List is empty!");
        }
        return response;
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMinistryConfirmedMember(UUID membroId, LocalDateTime start, LocalDateTime end) {
        return this.eventScaleRepository.findConfirmedScalesByMemberAndDateRange(membroId, start, end);
    }

    @Override
    public List<SimpleScaleResponse> listScaleMinistryIdsByMembroIdThatConfirmed(UUID membroId, LocalDateTime start, LocalDateTime end) {
        return this.eventScaleRepository.findScaleIdsByMemberIdParticipate(membroId, start, end);
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
    public List<NotConfirmedScaleMinistry> listReasonsAbsenceMemberEventByIdScalesMinistry(UUID idEscala) {
        return this.notConfirmedService.listAllNotConfirmedScaleMinistry();
    }

    @Override
    public void deleteSacleWithDeletingEvent(UUID idEvento) {
        log.info("Deleting scale when deleting evento " + idEvento + "...");
        List<EventScale> scales = this.eventScaleRepository.findByEventId(idEvento);
        for (EventScale scalesMinistry : scales) {
            if (scalesMinistry.getNotConfirmedMembers() != null) {
                scalesMinistry.getNotConfirmedMembers()
                        .stream()
                        .map(NotConfirmedScaleMinistry::getId)
                        .forEach(notConfirmedService::delete);
            }
            this.eventScaleRepository.delete(scalesMinistry);
            log.info("Escala " + scalesMinistry.getId() + " date " + scalesMinistry.getDate() + " deleted");
        }
    }

    @Override
    public ScaleEventResponseWithInfos addAndRemoveRepertoryInScale(
            UUID idScale, ScaleRepertoryDTO scaleRepertoryDTO) {
        log.info("Adding or removing repertorio of escala " + idScale + "...");

        log.info("Adding or removing repertorio from escala {}", idScale);

        EventScale scale = eventScaleRepository.findById(idScale)
                .orElseThrow(() -> new EventIllegalArumentException("Event Scale Not Found!"));

        List<RepertoryMinistry> repertories = new ArrayList<>(scale.getRepertories());

        if (scaleRepertoryDTO.getRepertoryIdsAdd() != null) {
            List<RepertoryMinistry> repertoriosParaAdicionar = repertoryMinistryRepository
                    .findAllById(scaleRepertoryDTO.getRepertoryIdsAdd());

            for (RepertoryMinistry r : repertoriosParaAdicionar) {
                if (!repertories.contains(r)) {
                    repertories.add(r);
                }
            }
        }
        if (scaleRepertoryDTO.getRepertoryIdsRemove() != null) {
            repertories.removeIf(r -> scaleRepertoryDTO.getRepertoryIdsRemove().contains(r.getId()));
        }

        scale.setRepertories(repertories);
        return this.scaleMapper.entityToResponseWithInfos(eventScaleRepository.save(scale));
    }

    @Override
    public EventScaleResponse getEventScaleById(UUID id) {
        return scaleMapper.entityToResponse(
                this.eventScaleRepository.findById(id).get()
        );
    }

}
