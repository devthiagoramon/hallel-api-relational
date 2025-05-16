package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ScaleEventResponseWithInfos {
    private UUID id;
    private UUID ministryId;
    private UUID eventId;
    private Date date;
    private List<UUID> membersMinistryInvitedIds;
    private List<UUID> membersMinistryConfirmedIds;
    private List<UUID> membersMinistryNotConfirmedIds;
    private List<UUID> repertoryIds;
    private UUID auditionMinistryId;
    private boolean isEnsaio;
}
