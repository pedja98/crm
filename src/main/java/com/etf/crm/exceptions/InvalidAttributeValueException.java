package com.etf.crm.exceptions;

public class InvalidAttributeValueException extends RuntimeException {
    public InvalidAttributeValueException(final String message) {
        super(message);
    }
}