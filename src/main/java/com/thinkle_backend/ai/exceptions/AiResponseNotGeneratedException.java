package com.thinkle_backend.ai.exceptions;

public class AiResponseNotGeneratedException extends RuntimeException{
    public AiResponseNotGeneratedException(String message) {
        super(message);
    }
}
