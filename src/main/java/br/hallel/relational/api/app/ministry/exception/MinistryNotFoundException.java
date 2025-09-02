package br.hallel.relational.api.app.ministry.exception;

import lombok.Getter;

@Getter
public class MinistryNotFoundException extends RuntimeException {
    private final String id;
    public MinistryNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }

}
