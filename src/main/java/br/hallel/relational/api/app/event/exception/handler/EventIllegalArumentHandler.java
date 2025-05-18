package br.hallel.relational.api.app.event.exception.handler;

import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.exception.EventScaleNotFoundException;
import br.hallel.relational.api.app.global.model.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
import java.util.Date;

@RestControllerAdvice
public class EventIllegalArumentHandler {
    @ExceptionHandler(value = EventIllegalArumentException.class)
    public ResponseEntity<ExceptionResponse> handleEventException(EventIllegalArumentException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EventScaleNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEventScaleException(EventScaleNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }
}
