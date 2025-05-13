package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.ministry.model.AuditionMinistry;
import br.hallel.relational.api.app.ministry.model.Ministry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventScaleResponse {

    private UUID id;
    private Event event;
    private Ministry ministry;
    private AuditionMinistry auditionMinistry;
    private Date date;
    private boolean isAudition;
    private List<UUID> membersMinistryInvitedIds;
    private List<UUID> membersMinistryConfirmeds;
    private List<UUID> membersMinistryNotConfirmedIds;
    private List<UUID> repertoryIds;
}
