package br.hallel.relational.api.app.association.exception;

public class UserAlreadyAssociatedException extends RuntimeException {
    public UserAlreadyAssociatedException(String message) {
        super(message);
    }
}
