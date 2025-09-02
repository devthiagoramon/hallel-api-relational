package br.hallel.relational.api.app.event.exception;

import lombok.Getter;

@Getter
public class EventNotFoundException extends RuntimeException {

    private final String eventId;

    public EventNotFoundException(String message, String eventId) {
        super(message);
        this.eventId = eventId;
    }
}
