package com.github.prgrms.socialserver.rest.dto;

/**
 * @author jiho
 * @since 2021/01/09
 */
public class UserRequestDto {
    private final String principal;
    private final String credentials;

    public UserRequestDto(String principal, String credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getCredentials() {
        return credentials;
    }
}
