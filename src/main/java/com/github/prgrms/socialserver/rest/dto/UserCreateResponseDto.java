package com.github.prgrms.socialserver.rest.dto;

/**
 * @author jiho
 * @since 2021/01/09
 */
public class UserCreateResponseDto {
    private final boolean success;
    private final Object response;

    public UserCreateResponseDto(boolean success, Object response) {
        this.success = success;
        this.response = response;
    }

    public boolean getSuccess() {
        return success;
    }

    public Object getResponse() {
        return response;
    }
}
