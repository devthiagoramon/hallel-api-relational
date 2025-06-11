package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.dto.mapper.EventScaleMapper;
import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.event.interfaces.ScaleInterface;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.model.MemberEventScaleStatus;
import br.hallel.relational.api.app.event.repository.EventScaleRepository;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse;
import br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse;
import br.hallel.relational.api.app.ministry.dto.mapper.MinistryMapper;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.MemberMinistryId;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.model.RepertoryMinistry;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventScaleService implements ScaleInterface {
    @Autowired
    private MemberMinistryRepository memberMinistryRepository;
    @Autowired
    private EventScaleRepository eventScaleRepository;
    @Autowired
    private MinistryService ministryService;
    @Autowired
    private MemberEventScaleService memberEventScaleService;
    @Autowired
    private MemberEventScaleRepository memberEventScaleRepository;

    private final MinistryMapper ministryMapper;
    private final EventScaleMapper scaleMapper;

    public EventScaleService(MinistryMapper ministryMapper, EventScaleMapper eventScaleMapper) {
        this.ministryMapper = ministryMapper;
        this.scaleMapper = eventScaleMapper;
    }

    @Override
    public EventScaleResponse createScale(Event event, UUID idMinistry) {
        log.info("Creating escala for ministerio " + idMinistry + "...");
        EventScale eventScale = new EventScale();

        Ministry ministry = ministryMapper.responseToEntity(ministryService.getMinistryById(idMinistry));
        eventScale.setMinistry(ministry);
        eventScale.setEvent(event);
        eventScale.setDate(event.getDate());
        EventScale eventScaleSaved = this.eventScaleRepository.save(eventScale);
        log.info("Escala " + eventScaleSaved.getId() + " created for event " + event.getTitle() + " to ministerio " + idMinistry);
        memberEventScaleService.inviteUserIntoScale(eventScaleSaved.getId(), ministry.getCoordinator().getId());
        memberEventScaleService.inviteUserIntoScale(eventScaleSaved.getId(), ministry.getViceCoordinator().getId());

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
    public EventScaleWithInfos getEventScaleWithInfos(UUID eventScaleId) {
        EventScale eventScale = this.eventScaleRepository.findById(eventScaleId).orElseThrow(
                () -> new EventScaleNotFoundException(
                        "Can't find escala with id " + eventScaleId
                )
        );

        List<MemberEventScale> membersInviteds =
                this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(MemberEventScaleStatus.CONVIDADO, eventScaleId);
        List<MemberEventScale> membersConfirmed =
                this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(MemberEventScaleStatus.PARTICIPANDO, eventScaleId);
        List<MemberEventScale> membersDecline =
                this.memberEventScaleRepository.findAllByStatusAndEventScale_Id(MemberEventScaleStatus.RECUSADO, eventScaleId);

        List<UUID> invitedIds = membersInviteds.stream()
                .map(member -> member.getUser().getId())
                .collect(Collectors.toList());
        List<UUID> confirmedIds = membersConfirmed.stream()
                .map(member -> member.getUser().getId())
                .collect(Collectors.toList());
        List<UUID> declinedIds = membersDecline.stream()
                .map(member -> member.getUser().getId())
                .collect(Collectors.toList());

        EventScaleWithInfos eventScaleWithInfos = new EventScaleWithInfos();
        eventScaleWithInfos.setId(eventScale.getId());
        eventScaleWithInfos.setAuditionMinistryId(eventScale.getMinistry().getId());
        eventScaleWithInfos.setEventId(eventScale.getEvent().getId());
        eventScaleWithInfos.setMinistryId(eventScale.getMinistry().getId());
        eventScaleWithInfos.setDate(eventScale.getDate());
        eventScaleWithInfos.setMembersInvited(invitedIds);
        eventScaleWithInfos.setMembersConfirmed(confirmedIds);
        eventScaleWithInfos.setMembersDecline(declinedIds);
        log.info("Get Event Scale With Infos");
        return eventScaleWithInfos;
    }

    public EventScaleResponse editDateScale(EventScale eventScale, Date date) {
        log.info("Editing escala date " + eventScale.getId() + "...");
        eventScale.setDate(date);
        EventScale scaleUpdated = this.eventScaleRepository.save(eventScale);
        return scaleMapper.entityToResponse(scaleUpdated);
    }

    public List<EventScaleResponse> editEventDate(UUID eventId, Date date) {
        log.info("Editing event date and updating scales of event {}", eventId);
        List<EventScale> scaleOfEvent = this.eventScaleRepository.findByEventId(eventId);
        List<EventScaleResponse> responses = new ArrayList<>();
        for (EventScale eventScale : scaleOfEvent) {
            responses.add(editDateScale(eventScale, date));
        }
        return responses;
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
    public List<ScaleEventWithEventInfoResponse> listScale() {
        log.info("Listing all the escala ministerio...");
        return this.eventScaleRepository.findAllWithEventosInfos();
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMemberIdCanParticipate(UUID memberId, LocalDateTime start, LocalDateTime end) {
//        return this.eventScaleRepository.findAllByMemberIdAndDateBetween(memberId, start, end);
        return null;
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMinistryIdsByMemberIdThatCanParticipate(UUID membroId, LocalDateTime start, LocalDateTime end) {
//        return this.eventScaleRepository.findEscalaMinisterioIdsByMembroIdCanPaticipate(membroId, start, end);
        return null;
    }


    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMinistryConfirmedMember(UUID membroId, LocalDateTime start, LocalDateTime end) {
//        return this.eventScaleRepository.findConfirmedScalesByMemberAndDateRange(membroId, start, end);
        return null;
    }

    @Override
    public List<SimpleScaleResponse> listScaleMinistryIdsByMembroIdThatConfirmed(UUID membroId, LocalDateTime start, LocalDateTime end) {
//        return this.eventScaleRepository.findScalesConfirmedByMemberIdAndDateRange(membroId, start, end);
        return null;
    }

    @Override
    public List<ScaleEventWithEventInfoResponse> listScaleMinistryRangeDate(LocalDateTime start, LocalDateTime end) {
//        return this.eventScaleRepository.findScalesInRangeDate(start, end);
        return null;
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
    public void deleteScaleWithDeletingEvent(UUID idEvento) {

    }

    @Override
    public ScaleEventResponseWithInfos addAndRemoveRepertoryInScale(UUID idEscalaMinisterio, ScaleRepertoryDTO escalaRepertorioDTO) {
        return null;
    }

    @Override
    public EventScaleResponse getEventScaleById(UUID id) {
        EventScale eventScale =
                this.eventScaleRepository.findById(id).orElseThrow(() ->
                        new EventScaleNotFoundException("Not find event scale by id " + id));

        return this.scaleMapper.entityToResponse(eventScale);
    }

    @Override
    public EventByEventScaleResponse getEventByEventScaleId(UUID id) {
        Event event = this.eventScaleRepository.findEventByEventScaleId(id);
        if (event == null) {
            throw new EventScaleNotFoundException("Not find event by id " + id +
                    "maybe it's wrong ");
        }
        return new EventByEventScaleResponse().toEventByEventScaleResponse(event);
    }

    @Override
    public List<MinistrySimpleResponse> listMinistriesByEventId(UUID eventId) {
        return this.eventScaleRepository.findMinistriesByEventId(eventId);
    }

    public ScaleEventResponseWithInfos addAndRemoveRepertoryInScala(String idEscalaMinisterio, ScaleRepertoryDTO escalaRepertorioDTO) {
        return null;
    }

    public List<EventScaleSimpleResponse> listEventsScalesByUserIdParticipate(
            UUID idMemberMinistry, LocalDateTime start, LocalDateTime end) {
        List<EventScaleSimpleResponse> response = new ArrayList<>();
        List<EventScale> eventScales = this.eventScaleRepository.findEscalaMinisterioIdsByMembroIdParticipate(idMemberMinistry, MemberEventScaleStatus.PARTICIPANDO, start, end);
        eventScales.forEach(event -> {
            response.add(new EventScaleSimpleResponse(
                    event.getId(), event.getDate()
            ));
        });
        log.info("Listing all the event Scale that Member Participate...");
        return response;
    }

    public List<String> listMembroMinisterioCanInviteToEscala(
            UUID eventScaleId, int page, int size) {
        EventScale eventScale = this.eventScaleRepository.findById(eventScaleId)
                .orElseThrow(() -> new EventScaleNotFoundException("Not find event scale by id " + eventScaleId));
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberMinistry> memberMinistryResponse =
                this.memberMinistryRepository.findMemberMinistriesByMinistry_Id(eventScale.getMinistry().getId(), pageable);

        List<UUID> convidados = this.memberEventScaleRepository
                .findAllByStatusAndEventScale_Id(MemberEventScaleStatus.CONVIDADO, eventScaleId)
                .stream()
                .map(m -> m.getUser().getId())
                .toList();

        List<UUID> participando = this.memberEventScaleRepository
                .findAllByStatusAndEventScale_Id(MemberEventScaleStatus.PARTICIPANDO, eventScaleId)
                .stream()
                .map(m -> m.getUser().getId())
                .toList();

        List<UUID> naoConfirmados = this.memberEventScaleRepository
                .findAllByStatusAndEventScale_Id(MemberEventScaleStatus.RECUSADO, eventScaleId)
                .stream()
                .map(m -> m.getUser().getId())
                .toList();

        return memberMinistryResponse.stream()
                .filter(m -> {
                    MemberMinistryId id = m.getId();
                    return !convidados.contains(id)
                            && !participando.contains(id)
                            && !naoConfirmados.contains(id);
                })
                .map(m -> m.getUser().getId().toString())
                .toList();
    }
}
