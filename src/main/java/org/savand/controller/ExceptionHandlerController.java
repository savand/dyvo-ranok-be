package org.savand.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler({SecurityException.class, UnsupportedOperationException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleArgumentNotValidException(Exception ex) {
        log.error(ex.toString());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return getObjectErrorResponseEntity(ex);
    }

    @NotNull
    private static ResponseEntity<Object> getObjectErrorResponseEntity(Exception ex) {

        String errorId = UUID.randomUUID().toString();

        log.error("ErrorId: {}", errorId, ex);

        return new ResponseEntity<>(String.format("Oops! Something went wrong :(\n" +
                "Please refer with this ID %s to the app administrator.", errorId),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}