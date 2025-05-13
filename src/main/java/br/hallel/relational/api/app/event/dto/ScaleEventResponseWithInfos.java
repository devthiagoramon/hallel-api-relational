package br.hallel.relational.api.app.event.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ScaleEventResponseWithInfos {
    private UUID id;
    private UUID ministryId;
    private UUID eventId;
    private Date date;
    private List<String> membersMinistryInvitedIds;
    private List<String> membersMinistryConfirmedIds;
    private List<String> membersMinistryNotConfirmedIds;
    private List<String> repertoryIds;
    private UUID auditionMinistryId;
    private boolean isEnsaio;
}
