package br.hallel.relational.api.app.event.exception;

public class EventTransactionNotFoundException extends RuntimeException {
    public EventTransactionNotFoundException(String message) {
        super(message);
    }
}
