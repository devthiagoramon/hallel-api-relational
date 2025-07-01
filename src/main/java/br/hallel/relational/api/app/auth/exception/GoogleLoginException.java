package br.hallel.relational.api.app.auth.exception;

public class GoogleLoginException extends RuntimeException {
    public GoogleLoginException(String message) {
        super(message);
    }
}
