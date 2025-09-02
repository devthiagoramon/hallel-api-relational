package br.hallel.relational.api.app.event.exception;

import lombok.Getter;

@Getter
public class EventTransactionNotFoundException extends RuntimeException {
    private final String id;
    public EventTransactionNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }
}
