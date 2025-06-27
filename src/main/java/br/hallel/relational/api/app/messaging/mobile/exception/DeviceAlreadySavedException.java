package br.hallel.relational.api.app.messaging.mobile.exception;

public class DeviceAlreadySavedException extends RuntimeException {
    public DeviceAlreadySavedException(String message) {
        super(message);
    }
}
