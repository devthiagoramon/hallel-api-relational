package br.hallel.relational.api.app.event.exception;

public class EventBatchSoldOutException extends RuntimeException {
    public EventBatchSoldOutException(String message) {
        super(message);
    }
}
