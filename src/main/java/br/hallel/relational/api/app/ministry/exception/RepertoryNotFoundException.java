package br.hallel.relational.api.app.ministry.exception;

import lombok.Getter;

@Getter
public class RepertoryNotFoundException extends RuntimeException {
    private final String id;
    public RepertoryNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }
}
