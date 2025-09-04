package br.hallel.relational.api.app.payment.checkout_transparent.exceptions;

public class MercadoPagoAPIException extends RuntimeException {
    public MercadoPagoAPIException(String message) {
        super(message);
    }
}
