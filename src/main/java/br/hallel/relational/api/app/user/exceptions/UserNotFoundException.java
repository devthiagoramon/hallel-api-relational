package br.hallel.relational.api.app.user.exceptions;


import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String userId;
    public UserNotFoundException(String message, String userId) {
        super(message);
        this.userId = userId;
    }
}
