package com.thinkle_backend.exceptions;

public class InvalidWordException extends RuntimeException {
    public InvalidWordException(String message) {
        super(message);
    }
}
