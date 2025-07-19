package com.thinkle_backend.exceptions;

public class HintTypeDoesNotExistsException extends RuntimeException{
    public HintTypeDoesNotExistsException(String message) {
        super(message);
    }
}
