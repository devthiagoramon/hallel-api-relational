package br.hallel.relational.api.app.event.exception;

public class EventListIsEmptyException extends RuntimeException {
    public EventListIsEmptyException(String message) {
        super(message);
    }
}
