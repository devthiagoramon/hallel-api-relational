package br.hallel.relational.api.app.ministry.exception.handler;

import br.hallel.relational.api.app.global.model.ExceptionResponse;
import br.hallel.relational.api.app.ministry.exception.MemberMinistryRegisterNotFoundException;
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import br.hallel.relational.api.app.ministry.exception.RepertoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class MinistryExcpetionHandler {

    @ExceptionHandler(value = MinistryIllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleMinistryIllegalArgument(MinistryIllegalArgumentException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MemberMinistryRegisterNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleMemberMinistryRegisterNotFoundException(MemberMinistryRegisterNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RepertoryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleRepertoryIllegalArgument(RepertoryNotFoundException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

}
