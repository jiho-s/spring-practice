package com.github.prgrms.socialserver.rest.dto;

/**
 * @author jiho
 * @since 2021/01/09
 */
public class CommonResponseDto {
    private final boolean success;
    private final Object response;

    public CommonResponseDto(boolean success, Object response) {
        this.success = success;
        this.response = response;
    }

    public static CommonResponseDto success(Object response) {
        return new CommonResponseDto(
                true,
                response
        );
    }

    public static CommonResponseDto fail(Object response) {
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
