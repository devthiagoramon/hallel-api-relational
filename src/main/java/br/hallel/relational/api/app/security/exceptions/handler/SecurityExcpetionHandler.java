package br.hallel.relational.api.app.security.exceptions.handler;

import br.hallel.relational.api.app.global.model.ExceptionResponse;
import br.hallel.relational.api.app.security.exceptions.CredentialsAuthException;
import br.hallel.relational.api.app.security.exceptions.InvalidJwtAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class SecurityExcpetionHandler {

    @ExceptionHandler(value = CredentialsAuthException.class)
    public ResponseEntity<ExceptionResponse> handleCredentialsAuthException(CredentialsAuthException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(value = InvalidJwtAuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidJwtAuthenticationException(InvalidJwtAuthenticationException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }
}
