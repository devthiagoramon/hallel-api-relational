package br.hallel.relational.api.app.event.dto;

import java.util.List;

public record ParticipationListResponse(
        List<EventParticipationResponse> participations,
        int total
) {}