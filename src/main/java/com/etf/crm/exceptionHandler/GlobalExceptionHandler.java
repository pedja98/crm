package com.etf.crm.exceptionHandler;

import com.etf.crm.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ItemNotFoundException.class,
            DuplicateItemException.class,
            PropertyCopyException.class,
            InvalidAttributeValueException.class,
            RuntimeException.class,
            BadRequestException.class
    })
    public ResponseEntity<String> handleExceptions(Exception ex) {
        HttpStatus status = switch (ex.getClass().getSimpleName()) {
            case "ItemNotFoundException" -> HttpStatus.NOT_FOUND;
            case "DuplicateItemException" -> HttpStatus.CONFLICT;
            case "PropertyCopyException",
                 "InvalidAttributeValueException",
                 "BadRequestException" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status).body(ex.getMessage());
    }
}
