package com.etf.crm.exceptions;

public class DuplicateItemException extends RuntimeException {
    public DuplicateItemException(final String message) {
        super(message);
    }
}
