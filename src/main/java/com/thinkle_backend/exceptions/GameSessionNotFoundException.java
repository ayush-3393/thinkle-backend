package com.thinkle_backend.exceptions;

public class GameSessionNotFoundException extends RuntimeException{
    public GameSessionNotFoundException(String message) {
        super(message);
    }
}
