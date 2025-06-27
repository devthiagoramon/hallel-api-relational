package br.hallel.relational.api.app.messaging.mobile.exception.handler;

import br.hallel.relational.api.app.global.exception.UUIDFormatException;
import br.hallel.relational.api.app.global.model.ExceptionResponse;
import br.hallel.relational.api.app.messaging.mobile.exception.DeviceAlreadySavedException;
import br.hallel.relational.api.app.messaging.mobile.exception.DeviceNotificationNotFoundException;
import br.hallel.relational.api.app.messaging.mobile.exception.MessageFormatterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;

@RestControllerAdvice
public class MessagingMobileExceptionHandler {

    @ExceptionHandler(value = DeviceNotificationNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleDeviceNotificationNotFoundException(DeviceNotificationNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DeviceAlreadySavedException.class)
    public ResponseEntity<ExceptionResponse> handleDeviceAlreadySavedException(DeviceNotificationNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = MessageFormatterException.class)
    public ResponseEntity<ExceptionResponse> handleDeviceAlreadySavedException(MessageFormatterException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
