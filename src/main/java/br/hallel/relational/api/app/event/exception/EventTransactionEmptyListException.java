package br.hallel.relational.api.app.event.exception;

public class EventTransactionEmptyListException extends RuntimeException {
    private final String eventId;

    public EventTransactionEmptyListException(String messageKey, String eventId) {
        super(messageKey);
        this.eventId = eventId;
    }

    // Método que o Handler está tentando chamar
    public String getEventId() {
        return eventId;
    }
}