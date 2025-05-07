package br.hallel.relational.api.app.auth.exception;

public class AuthRequestException extends RuntimeException {
    public AuthRequestException(String message) {
        super(message);
    }
}
