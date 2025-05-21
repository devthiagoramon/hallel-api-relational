package br.hallel.relational.api.app.event.dto;

public record AcceptOrDeclineMemberInScale(
        Boolean isAccept,
        String reason_decline
) {
}
