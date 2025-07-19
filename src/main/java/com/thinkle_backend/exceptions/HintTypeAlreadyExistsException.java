package com.thinkle_backend.exceptions;

public class HintTypeAlreadyExistsException extends RuntimeException{
    public HintTypeAlreadyExistsException(String message) {
        super(message);
    }
}
