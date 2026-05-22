package br.hallel.relational.api.app.payment.pix_config.exception.handler;

import br.hallel.relational.api.app.global.model.ExceptionResponse;
import br.hallel.relational.api.app.payment.pix_config.exception.PixConfigNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class PixConfigExceptionHandler {

    @ExceptionHandler(PixConfigNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(
            PixConfigNotFoundException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalState(
            IllegalStateException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
