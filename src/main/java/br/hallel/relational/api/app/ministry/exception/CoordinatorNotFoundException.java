package br.hallel.relational.api.app.ministry.exception;

import lombok.Getter;

@Getter
public class CoordinatorNotFoundException extends RuntimeException {
    private final String id;
    public CoordinatorNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }
}
