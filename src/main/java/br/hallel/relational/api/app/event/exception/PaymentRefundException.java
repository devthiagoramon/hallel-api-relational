package br.hallel.relational.api.app.event.exception;

public class PaymentRefundException extends RuntimeException {
    public PaymentRefundException(String message) {
        super(message);
    }
}
