package com.dws.challenge.exception;

public class InvalidAmountException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -7947189583145486822L;

    public InvalidAmountException(){

    }

    public InvalidAmountException(String message){
        super(message);
    }
}
