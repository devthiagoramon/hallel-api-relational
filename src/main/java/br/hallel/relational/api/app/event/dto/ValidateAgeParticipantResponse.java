package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.enum_type.AgeGroup;

public record ValidateAgeParticipantResponse(
        AgeGroup ageGroup,
        AgeGroup limiteReached
) {
}
