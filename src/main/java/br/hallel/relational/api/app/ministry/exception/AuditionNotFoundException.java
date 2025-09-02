package br.hallel.relational.api.app.ministry.exception;

import lombok.Getter;

@Getter
public class AuditionNotFoundException extends RuntimeException {
    private final String id;

    public AuditionNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }
}
