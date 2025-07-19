package com.thinkle_backend.exceptions;

public class HintDoesNotExistsException extends RuntimeException{
    public HintDoesNotExistsException(String message) {
        super(message);
    }
}
