package br.hallel.relational.api.app.event.exception;

import lombok.Getter;

@Getter
public class EventParticipationException extends RuntimeException {

    public EventParticipationException(String message) {
        super(message);

    }
}
