package com.dws.challenge.exception;

public class LockException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -4928284032964776178L;

    public LockException(final String message) {
        super(message);
    }
}