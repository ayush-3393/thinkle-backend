package com.thinkle_backend.exceptions;

public class WordAlreadyExistsException extends RuntimeException {
    public WordAlreadyExistsException(String message) {
        super(message);
    }
}
