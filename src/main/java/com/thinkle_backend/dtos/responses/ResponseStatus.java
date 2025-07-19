package com.thinkle_backend.dtos.responses;

import lombok.Getter;

@Getter
public enum ResponseStatus {
    SUCCESS(0),
    FAILURE(1);

    private final int code;

    ResponseStatus(int code) {
        this.code = code;
    }
}
