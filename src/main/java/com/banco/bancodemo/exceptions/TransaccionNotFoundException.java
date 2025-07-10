package com.banco.bancodemo.exceptions;

public class TransaccionNotFoundException extends RuntimeException {
    public TransaccionNotFoundException(String message) {
        super(message);
    }

    public TransaccionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
