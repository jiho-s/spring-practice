package com.github.prgrms.socialserver.rest.dto;

/**
 * @author jiho
 * @since 2021/01/09
 */
public class CommonResponseDto<T> {
    private final boolean success;
    private final T response;

    public CommonResponseDto(boolean success, T response) {
        this.success = success;
        this.response = response;
    }

    public static <T> CommonResponseDto<T> success(T response) {
        return new CommonResponseDto<T>(
                true,
                response
        );
    }

    public static <T> CommonResponseDto<T> fail(T response) {
        return new CommonResponseDto(
                false,
                response
        );
    }

    public boolean getSuccess() {
        return success;
    }

    public Object getResponse() {
        return response;
    }
}
