package br.hallel.relational.api.app.user.exceptions.handler;

import br.hallel.relational.api.app.global.model.ExceptionResponse;
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
    private final MessageSource messageSource;


    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundExcpetion(
            UserNotFoundException exception, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();

        String messageKey = exception.getMessage();
        Object[] args = new Object[]{exception.getUserId()};

        String localizedMessage = messageSource.getMessage(messageKey, args, locale);

        ExceptionResponse response = new ExceptionResponse(
                localizedMessage,
                new Date(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
