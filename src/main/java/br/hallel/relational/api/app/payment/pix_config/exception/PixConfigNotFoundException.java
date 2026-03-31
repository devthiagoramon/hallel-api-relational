package br.hallel.relational.api.app.payment.pix_config.exception;

public class PixConfigNotFoundException extends RuntimeException {
    public PixConfigNotFoundException(String message) {
        super(message);
    }
}
