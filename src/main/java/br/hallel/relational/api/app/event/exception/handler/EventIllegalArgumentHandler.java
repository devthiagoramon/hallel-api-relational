package br.hallel.relational.api.app.event.exception.handler;

import br.hallel.relational.api.app.event.exception.*;
import br.hallel.relational.api.app.global.model.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;

@RestControllerAdvice
public class EventIllegalArgumentHandler {
    @ExceptionHandler(value = EventIllegalArumentException.class)
    public ResponseEntity<ExceptionResponse> handleEventException(EventIllegalArumentException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ListEventScaleIsEmpty.class)
    public ResponseEntity<ExceptionResponse> handleListEventScaleIsEmpty(ListEventScaleIsEmpty exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = EventScaleNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEventScaleException(EventScaleNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleEventScaleArgumentException(MethodArgumentTypeMismatchException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                exception.getMessage(), new Date(), request.getDescription(false)
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventScaleIllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleScaleIllegalArgumentException(EventScaleIllegalArgumentException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                exception.getMessage(),
                new Date(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MemberEventScaleNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMemberEventScaleNotFoundException(MemberEventScaleNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MemberScaleAlreadyHasThatStatus.class)
    public ResponseEntity<ExceptionResponse> handleMemberScaleAlreadyHasThatStatus(MemberScaleAlreadyHasThatStatus exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MemberEventScaleIllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleMemberEventScaleIllegalArgumentException(MemberEventScaleIllegalArgumentException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InviteEventScaleException.class)
    public ResponseEntity<ExceptionResponse> handleInviteEventScaleException(InviteEventScaleException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventScaleRuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleEventScaleRuntimeException(EventScaleRuntimeException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEventNotFoundException(EventNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventTransactionNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEventTransactionNotFoundException(EventTransactionNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventParticipationException.class)
    public ResponseEntity<ExceptionResponse> handleEventParticipationException(EventParticipationException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventTransactionEmptyListException.class)
    public ResponseEntity<ExceptionResponse> handleEventTransactionEmptyListException(EventTransactionEmptyListException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ExceptionResponse> handleUserValidationException(UserValidationException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentRefundException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentRefundException(PaymentRefundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
