package br.hallel.relational.api.app.event.dto;

public record EventPayParticipationDetails(
        String qrCode,
        String copyAndPaste,
        double value
) {
}
