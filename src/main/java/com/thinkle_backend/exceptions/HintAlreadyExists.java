package com.thinkle_backend.exceptions;

public class HintAlreadyExists extends RuntimeException{
    public HintAlreadyExists(String message) {
        super(message);
    }
}
