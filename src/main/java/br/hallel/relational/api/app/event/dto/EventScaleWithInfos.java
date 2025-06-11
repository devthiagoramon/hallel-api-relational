package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventScaleWithInfos {
    private UUID id;
    private UUID eventId;
    private UUID ministryId;
    private UUID auditionMinistryId;
    private Date date;
    private List<UUID> membersInvited;
    private List<UUID> membersConfirmed;
    private List<UUID> membersDecline;
    private List<UUID> repertoryIds;
}
