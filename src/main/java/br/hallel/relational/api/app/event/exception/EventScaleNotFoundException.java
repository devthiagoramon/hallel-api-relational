package br.hallel.relational.api.app.event.exception;

import lombok.Getter;

@Getter
public class EventScaleNotFoundException extends RuntimeException {
    private final String id;
    public EventScaleNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }
}
