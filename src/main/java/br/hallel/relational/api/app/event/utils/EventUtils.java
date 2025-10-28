package br.hallel.relational.api.app.event.utils;

import br.hallel.relational.api.app.event.dto.EventInviteDTO;
import br.hallel.relational.api.app.event.dto.EventScheduleDTO;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventInvite;
import br.hallel.relational.api.app.event.model.EventSchedule;
import br.hallel.relational.api.app.ministry.exception.MinistryNotFoundException;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.repository.MinistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventUtils {

    private final MinistryRepository ministryRepository;

    public void synchronizeEventInvites(Event event, List<EventInviteDTO> dtos) {
        Map<UUID, EventInvite> existingInvitesMap = event.getEventInvites().stream()
                .collect(Collectors.toMap(EventInvite::getId, eventInvite -> eventInvite));

        Set<UUID> processedInviteIds = new HashSet<>();
        for (EventInviteDTO dto : dtos) {
            if (dto.getId() == null) {
                EventInvite newInvite = new EventInvite();
                newInvite.setName(dto.getName());
                newInvite.setDescription(dto.getDescription());
                newInvite.setValue(dto.getValue());
                newInvite.setEvent(event);
                event.addInvite(newInvite);
            } else {
                processedInviteIds.add(dto.getId());
                EventInvite existingInvite = existingInvitesMap.get(dto.getId());
                if (existingInvite != null) {
                    existingInvite.setName(dto.getName());
                    existingInvite.setDescription(dto.getDescription());
                    existingInvite.setValue(dto.getValue());
                    existingInvite.setEvent(event);
                }
            }
        }
        event.getEventInvites()
                .removeIf(invite -> invite.getId() != null && !processedInviteIds.contains(invite.getId()));

    }

    public void synchronizeEventSchedules(Event event, List<EventScheduleDTO> dtos) {
        Map<UUID, EventSchedule> existingScheduleMap = event.getEventSchedules().stream()
                .collect(Collectors.toMap(EventSchedule::getId, eventSchedule -> eventSchedule));

        Set<UUID> processedSchedulesIds = new HashSet<>();
        for (EventScheduleDTO dto : dtos) {
            if (dto.getId() == null) {
                EventSchedule newSchedule = new EventSchedule();
                newSchedule.setDescription(dto.getDescription());
                if (dto.getMinistryId() != null) {
                    Ministry ministry = ministryRepository.findById(dto.getMinistryId()).orElseThrow(
                            () -> new MinistryNotFoundException(
                                    "Não foi possivel encontrar o ministério para associar a programação do evento",
                                    dto.getMinistryId().toString()));
                    newSchedule.setMinistry(ministry);
                }
                newSchedule.setCreatedAt(OffsetDateTime.now());
                newSchedule.setDate(dto.getDate());
                event.addSchedule(newSchedule);
            } else {
                processedSchedulesIds.add(dto.getId());
                EventSchedule existingSchedule = existingScheduleMap.get(dto.getId());
                if (existingSchedule != null) {
                    existingSchedule.setDescription(dto.getDescription());
                    if (dto.getMinistryId() != null) {
                        Ministry ministry = ministryRepository.findById(dto.getMinistryId()).orElseThrow(
                                () -> new MinistryNotFoundException(
                                        "Não foi possivel encontrar o ministério para associar a programação do evento",
                                        dto.getMinistryId().toString()));
                        existingSchedule.setMinistry(ministry);
                    }
                    existingSchedule.setDate(dto.getDate());
                    existingSchedule.setEditedAt(OffsetDateTime.now());

                }
            }
        }
        event.getEventSchedules()
                .removeIf(schedule -> schedule.getId() != null && !processedSchedulesIds.contains(schedule.getId()));
    }


}
