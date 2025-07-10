package com.banco.bancodemo.exceptions;

public class TitularCuentaNotFoundException extends RuntimeException {
    public TitularCuentaNotFoundException(String message) {
        super(message);
    }

    public TitularCuentaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
