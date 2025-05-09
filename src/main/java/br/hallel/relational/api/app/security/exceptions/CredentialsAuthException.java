package br.hallel.relational.api.app.security.exceptions;

public class CredentialsAuthException extends RuntimeException {
    public CredentialsAuthException(String message) {
        super(message);
    }
}
