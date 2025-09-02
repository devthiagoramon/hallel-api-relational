package br.hallel.relational.api.app.event.dto;

import org.springframework.data.domain.Page;

public record ParticipationListResponse(
        Page<EventParticipationResponse> participations,
        int total
) {}