package br.hallel.relational.api.app.global.exception.handler;

import br.hallel.relational.api.app.global.exception.UUIDFormatException;
import br.hallel.relational.api.app.global.model.ExceptionResponse;
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(IllegalArgumentException exception, WebRequest request) {
        if (exception.getMessage().startsWith("Invalid UUID string")) {
            throw new UUIDFormatException("Invalid UUID format, try again");
        }
        throw exception;
    }

    @ExceptionHandler(value = UUIDFormatException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(MinistryIllegalArgumentException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
