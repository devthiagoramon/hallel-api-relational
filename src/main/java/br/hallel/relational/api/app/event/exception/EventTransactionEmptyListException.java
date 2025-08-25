package br.hallel.relational.api.app.event.exception;

public class EventTransactionEmptyListException extends RuntimeException {
    public EventTransactionEmptyListException(String message) {
        super(message);
    }
}
