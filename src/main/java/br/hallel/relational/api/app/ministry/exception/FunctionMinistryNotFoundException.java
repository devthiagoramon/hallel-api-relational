package br.hallel.relational.api.app.ministry.exception;

public class FunctionMinistryNotFoundException extends RuntimeException {
    private final String id;
    public FunctionMinistryNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }
}
