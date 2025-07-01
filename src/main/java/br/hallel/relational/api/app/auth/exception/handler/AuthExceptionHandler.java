package br.hallel.relational.api.app.auth.exception.handler;

import br.hallel.relational.api.app.auth.exception.AuthRequestException;
import br.hallel.relational.api.app.auth.exception.GoogleLoginException;
import br.hallel.relational.api.app.global.model.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
import java.util.Date;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(value = AuthRequestException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthRequestException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
 @ExceptionHandler(value = GoogleLoginException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(GoogleLoginException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
