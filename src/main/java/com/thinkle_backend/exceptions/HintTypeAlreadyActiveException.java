package com.thinkle_backend.exceptions;

public class HintTypeAlreadyActiveException extends RuntimeException{
    public HintTypeAlreadyActiveException(String message) {
        super(message);
    }
}
