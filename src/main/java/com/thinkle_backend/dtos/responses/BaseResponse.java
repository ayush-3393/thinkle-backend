package com.thinkle_backend.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private int statusCode;  // 0 = success, 1 = failure
    private String message;
    private List<T> data;


    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, "Success", List.of(data));
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(0, message, List.of(data));
    }

    public static <T> BaseResponse<T> failure(String message) {
        return new BaseResponse<>(1, message, Collections.emptyList());
    }

    public static <T> BaseResponse<T> failure(String message, T data) {
        return new BaseResponse<>(1, message, List.of(data));
    }
}
