package br.hallel.relational.api.app.event.exception;

public class FoodEmptyListException extends RuntimeException {
    public FoodEmptyListException(String message) {
        super(message);
    }
}
