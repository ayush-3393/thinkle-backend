package com.thinkle_backend.exceptions;

public class CanNotSubmitGuessException extends RuntimeException{
    public CanNotSubmitGuessException(String message) {
        super(message);
    }
}
