package br.hallel.relational.api.app.payment.checkout_transparent.exceptions.handler;

import br.hallel.relational.api.app.global.model.ExceptionResponse;
import br.hallel.relational.api.app.payment.checkout_transparent.exceptions.GenerateReceiptException;
import br.hallel.relational.api.app.payment.checkout_transparent.exceptions.MercadoPagoAPIException;
import br.hallel.relational.api.app.payment.checkout_transparent.exceptions.MercadoPagoException;
import br.hallel.relational.api.app.security.exceptions.CredentialsAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(value = MercadoPagoException.class)
    public ResponseEntity<ExceptionResponse> handleMercadoPagoException(MercadoPagoException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(value = MercadoPagoAPIException.class)
    public ResponseEntity<ExceptionResponse> handleMercadoPagoAPIException(MercadoPagoAPIException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(value = GenerateReceiptException.class)
    public ResponseEntity<ExceptionResponse> handleGenerationReceiptException(GenerateReceiptException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
