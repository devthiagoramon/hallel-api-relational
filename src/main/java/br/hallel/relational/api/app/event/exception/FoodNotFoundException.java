package br.hallel.relational.api.app.event.exception;

import lombok.Getter;

@Getter
public class FoodNotFoundException extends RuntimeException {
    private final String id;
    public FoodNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }
}
