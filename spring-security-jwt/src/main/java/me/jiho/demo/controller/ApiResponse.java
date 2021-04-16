package me.jiho.demo.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author jiho
 * @since 2021/04/16
 */
@RequiredArgsConstructor
@Getter
public class ApiResponse<T> {

    public static ApiResponse<?> UNAUTHORIZED = ApiResponse.error("Authentication error (cause: unauthorized)", HttpStatus.UNAUTHORIZED);

    private final boolean success;

    private final T response;

    private final ApiError error;

    public static <T> ApiResponse<T> ok(T response) {
        return new ApiResponse<>(true, response, null);
    }

    public static ApiResponse<?> error(Throwable throwable, HttpStatus status) {
        return new ApiResponse<>(false, null, new ApiError(throwable, status));
    }

    public static ApiResponse<?> error(String errorMessage, HttpStatus status) {
        return new ApiResponse<>(false, null, new ApiError(errorMessage, status));
    }
}
