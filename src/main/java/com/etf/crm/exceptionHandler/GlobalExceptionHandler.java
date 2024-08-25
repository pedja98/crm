package com.etf.crm.exceptionHandler;

import com.etf.crm.exceptions.DuplicateItemException;
import com.etf.crm.exceptions.ItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ItemNotFoundException.class, DuplicateItemException.class})
    public ResponseEntity<String> handleExceptions(RuntimeException ex) {
        HttpStatus status;

        if (ex instanceof ItemNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof DuplicateItemException) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(status).body(ex.getMessage());
    }
}
