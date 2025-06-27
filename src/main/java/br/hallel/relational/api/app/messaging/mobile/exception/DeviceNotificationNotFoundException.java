package br.hallel.relational.api.app.messaging.mobile.exception;

public class DeviceNotificationNotFoundException extends RuntimeException {
    public DeviceNotificationNotFoundException(String message) {
        super(message);
    }
}
