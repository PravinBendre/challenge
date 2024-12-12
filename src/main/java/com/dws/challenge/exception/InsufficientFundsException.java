package com.dws.challenge.exception;

public class InsufficientFundsException extends RuntimeException {

    private static final long serialVersionUID = 4090867331100212991L;

    public InsufficientFundsException(String message) {
        super(message);
    }
}
