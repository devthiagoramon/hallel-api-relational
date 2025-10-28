package br.hallel.relational.api.app.user.exceptions.handler;

import br.hallel.relational.api.app.global.model.ExceptionResponse;
import br.hallel.relational.api.app.user.exceptions.RoleNotFoundException;
import br.hallel.relational.api.app.user.exceptions.UpdateRoleUserException;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class UserExceptionHandler {


    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundExcpetion(
            UserNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UpdateRoleUserException.class)
    public ResponseEntity<ExceptionResponse> handleUpdateRoleUserExcpetion(UpdateRoleUserException exception,
                                                                           WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RoleNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleRoleNotFoundExcpetion(
            RoleNotFoundException exception, WebRequest request
    ) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }
}
