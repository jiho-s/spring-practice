package me.jiho.demo.controller;

import lombok.RequiredArgsConstructor;

/**
 * @author jiho
 * @since 2021/04/16
 */
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final boolean success;

    private final T response;

    private final ApiError error;

    public static <T> ApiResponse<T> ok(T response) {
        return new ApiResponse<>(true, response, null);
    }

}
