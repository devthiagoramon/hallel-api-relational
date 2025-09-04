package br.hallel.relational.api.app.payment.checkout_transparent.exceptions;

public class GenerateReceiptException extends RuntimeException {
    public GenerateReceiptException(String message) {
        super(message);
    }
}
