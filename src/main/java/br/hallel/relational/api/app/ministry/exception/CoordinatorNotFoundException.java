package br.hallel.relational.api.app.ministry.exception;

public class CoordinatorNotFoundException extends RuntimeException {
    public CoordinatorNotFoundException(String message) {
        super(message);
    }
}
