package com.thinkle_backend.exceptions;

public class WordDoesNotExistsException extends RuntimeException{
    public WordDoesNotExistsException(String message) {
        super(message);
    }
}
