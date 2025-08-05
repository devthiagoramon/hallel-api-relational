package br.hallel.relational.api.app.global.exception.handler;

import br.hallel.relational.api.app.global.exception.*;
import br.hallel.relational.api.app.global.model.ExceptionResponse;
import br.hallel.relational.api.app.ministry.exception.MinistryIllegalArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidArg1Exception(IllegalArgumentException exception, WebRequest request) {
        if (exception.getMessage().startsWith("Invalid UUID string")) {
            throw new UUIDFormatException("Invalid UUID format, try again");
        }
        throw exception;
    }

    @ExceptionHandler(value = UUIDFormatException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidUUIDFormatException(MinistryIllegalArgumentException exception, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleMaxSizeException(MaxUploadSizeExceededException exc, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exc.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(FileNullException.class)
    public ResponseEntity<ExceptionResponse> handleFileNullException(FileNullException exc, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exc.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SendImageBucketException.class)
    public ResponseEntity<ExceptionResponse> handleSendImageBucketException(SendImageBucketException exc, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exc.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DeleteImageBucketException.class)
    public ResponseEntity<ExceptionResponse> handleDeleteImageBucketException(DeleteImageBucketException exc, WebRequest request){
        ExceptionResponse exceptionResponse = new ExceptionResponse(exc.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EditImageBucketException.class)
    public ResponseEntity<ExceptionResponse> handleEditImageBucketException(EditImageBucketException exc, WebRequest request){
        ExceptionResponse exceptionResponse = new ExceptionResponse(exc.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
