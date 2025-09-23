package br.hallel.relational.api.app.ministry.exception.handler;

import br.hallel.relational.api.app.global.model.ExceptionResponse;
import br.hallel.relational.api.app.ministry.exception.*;
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
public class MinistryExcpetionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(value = MinistryIllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleMinistryIllegalArgument(MinistryIllegalArgumentException exception,
                                                                           WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MemberMinistryRegisterNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMemberMinistryRegisterNotFoundException(
            MemberMinistryRegisterNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RepertoryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleRepertoryIllegalArgument(RepertoryNotFoundException exception,
                                                                            WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageKey = exception.getMessage();
        Object[] args = new Object[]{exception.getId()};

        String localizedMessage = messageSource.getMessage(messageKey, args, locale);

        ExceptionResponse response = new ExceptionResponse(
                localizedMessage,
                new Date(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = FunctionMinistryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleFunctionMinistyrNotFound(FunctionMinistryNotFoundException exception,
                                                                            WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ListRepertoryEmptyException.class)
    public ResponseEntity<ExceptionResponse> listRepertoryEmptyException(ListRepertoryEmptyException exception,
                                                                         WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(value = CoordinatorNotFoundException.class)
    public ResponseEntity<ExceptionResponse> coordinatorNotFoundException(CoordinatorNotFoundException exception,
                                                                          WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageKey = exception.getMessage();
        Object[] args = new Object[]{exception.getId()};

        String localizedMessage = messageSource.getMessage(messageKey, args, locale);

        ExceptionResponse response = new ExceptionResponse(
                localizedMessage,
                new Date(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MemberAuditionMinistryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> memberAuditionMinistryNotFound(MemberAuditionMinistryNotFoundException exception,
                                                                            WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MinistryListEmptyException.class)
    public ResponseEntity<ExceptionResponse> ministryListEmptyException(MinistryListEmptyException exception,
                                                                        WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(value = RoleMinistryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> roleMinistryNotFoundException(RoleMinistryNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(value = ScaleChatParticipantNotFoundException.class)
    public ResponseEntity<ExceptionResponse> scaleChatParticipantNotFoundException(ScaleChatParticipantNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ScaleChatMessageNotFound.class)
    public ResponseEntity<ExceptionResponse> scaleChatMessageNotFound(ScaleChatMessageNotFound exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MinistryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMinistryNotFoundException(MinistryNotFoundException exception, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageKey = exception.getMessage();
        Object[] args = new Object[]{exception.getId()};

        String localizedMessage = messageSource.getMessage(messageKey, args, locale);

        ExceptionResponse response = new ExceptionResponse(
                localizedMessage,
                new Date(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AuditionNotFoundException.class)
    public ResponseEntity<ExceptionResponse> scaleChatMessageNotFound(AuditionNotFoundException exception, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageKey = exception.getMessage();
        Object[] args = new Object[]{exception.getId()};

        String localizedMessage = messageSource.getMessage(messageKey, args, locale);

        ExceptionResponse response = new ExceptionResponse(
                localizedMessage,
                new Date(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AuditionListIsEmptyException.class)
    public ResponseEntity<ExceptionResponse> scaleChatMessageNotFound(AuditionListIsEmptyException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MessageStatusNotFoundException.class)
    public ResponseEntity<ExceptionResponse> messageStatusNotFoundException(MessageStatusNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

}
