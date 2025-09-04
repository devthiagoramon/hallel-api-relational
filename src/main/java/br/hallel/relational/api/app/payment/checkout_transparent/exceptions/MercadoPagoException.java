package br.hallel.relational.api.app.payment.checkout_transparent.exceptions;

public class MercadoPagoException extends RuntimeException {
    public MercadoPagoException(String message) {
        super(message);
    }
}
