package com.thinkle_backend.exceptions;

public class HintTypeAlreadyDeletedException extends RuntimeException{
    public HintTypeAlreadyDeletedException(String message) {
        super(message);
    }
}
