package br.hallel.relational.api.app.event.exception.handler;

import br.hallel.relational.api.app.event.exception.*;
import br.hallel.relational.api.app.global.model.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class EventIllegalArgumentHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(value = EventIllegalArumentException.class)
    public ResponseEntity<ExceptionResponse> handleEventException(EventIllegalArumentException exception,
                                                                  WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ListEventScaleIsEmpty.class)
    public ResponseEntity<ExceptionResponse> handleListEventScaleIsEmpty(ListEventScaleIsEmpty exception,
                                                                         WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = EventScaleNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEventScaleException(EventScaleNotFoundException exception,
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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleEventScaleArgumentException(
            MethodArgumentTypeMismatchException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                exception.getMessage(), new Date(), request.getDescription(false)
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventScaleIllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleScaleIllegalArgumentException(
            EventScaleIllegalArgumentException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                exception.getMessage(),
                new Date(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MemberEventScaleNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMemberEventScaleNotFoundException(
            MemberEventScaleNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MemberScaleAlreadyHasThatStatus.class)
    public ResponseEntity<ExceptionResponse> handleMemberScaleAlreadyHasThatStatus(
            MemberScaleAlreadyHasThatStatus exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MemberEventScaleIllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleMemberEventScaleIllegalArgumentException(
            MemberEventScaleIllegalArgumentException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InviteEventScaleException.class)
    public ResponseEntity<ExceptionResponse> handleInviteEventScaleException(InviteEventScaleException exception,
                                                                             WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventScaleRuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleEventScaleRuntimeException(EventScaleRuntimeException exception,
                                                                              WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEventNotFoundException(EventNotFoundException exception,
                                                                          WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();

        String messageKey = exception.getMessage();
        Object[] args = new Object[]{exception.getEventId()};

        String localizedMessage = messageSource.getMessage(messageKey, args, locale);

        ExceptionResponse response = new ExceptionResponse(
                localizedMessage,
                new Date(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EventTransactionNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEventTransactionNotFoundException(
            EventTransactionNotFoundException exception, WebRequest request) {
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

    @ExceptionHandler(EventParticipationException.class)
    public ResponseEntity<ExceptionResponse> handleEventParticipationException(EventParticipationException exception,
                                                                               WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventTransactionEmptyListException.class)
    public ResponseEntity<ExceptionResponse> handleEventTransactionEmptyListException(
            EventTransactionEmptyListException exception,
            WebRequest request
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageKey = exception.getMessage();
        Object[] args = new Object[]{exception.getEventId()};

        String localizedMessage = messageSource.getMessage(messageKey, args, locale);

        ExceptionResponse response = new ExceptionResponse(
                localizedMessage,
                new Date(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ExceptionResponse> handleUserValidationException(UserValidationException exception,
                                                                           WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentRefundException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentRefundException(PaymentRefundException exception,
                                                                          WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventListIsEmptyException.class)
    public ResponseEntity<ExceptionResponse> handleEventListIsEmpty(EventListIsEmptyException exception,
                                                                    WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageKey = exception.getMessage();

        String localizedMessage = messageSource.getMessage(messageKey, new Object[]{}, locale);

        ExceptionResponse response = new ExceptionResponse(
                localizedMessage,
                new Date(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FoodEmptyListException.class)
    public ResponseEntity<ExceptionResponse> handleFoodEmptyListException(FoodEmptyListException exception,
                                                                          WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageKey = exception.getMessage();

        String localizedMessage = messageSource.getMessage(messageKey, new Object[]{}, locale);

        ExceptionResponse response = new ExceptionResponse(
                localizedMessage,
                new Date(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FoodNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleFoodNotFoundException(FoodNotFoundException exception,
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

    @ExceptionHandler(GenerateParticipationsPDFException.class)
    public ResponseEntity<ExceptionResponse> handleGenereateParticipationsPDFException(
            GenerateParticipationsPDFException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<ExceptionResponse> handleGenerateEventTransactionPDFException(
            GenerateEventTransactionPDFException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EventInviteNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEventNotFoundExceptionHandler(
            EventInviteNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EventBatchSoldOutException.class)
    public ResponseEntity<ExceptionResponse> handleEventNotFoundExceptionHandler(
            EventBatchSoldOutException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
    }

}
