package br.hallel.relational.api.app.association.exception.handler;

import br.hallel.relational.api.app.association.exception.AssociateException;
import br.hallel.relational.api.app.association.exception.AssociateNotFoundException;
import br.hallel.relational.api.app.association.exception.UserAlreadyAssociatedException;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.global.model.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
@RequiredArgsConstructor
public class AssociateHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(value = UserAlreadyAssociatedException.class)
    public ResponseEntity<ExceptionResponse> handleUserAlreadyAssociatedException(UserAlreadyAssociatedException exception,
                                                                  WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AssociateNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleAssociateNotFoundException(AssociateNotFoundException exception,
                                                                  WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AssociateException.class)
    public ResponseEntity<ExceptionResponse> handleAssociateException(AssociateException exception,
                                                                  WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
