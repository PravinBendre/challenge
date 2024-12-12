package com.dws.challenge.exception;

public class InvalidAccountException extends RuntimeException {
    private static final long serialVersionUID = -6601745803948002536L;

    public InvalidAccountException(){

    }

    public InvalidAccountException(String message){
        super(message);
    }
}
