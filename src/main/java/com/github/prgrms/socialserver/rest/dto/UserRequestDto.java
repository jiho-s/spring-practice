package com.github.prgrms.socialserver.rest.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jiho
 * @since 2021/01/09
 */
public class UserRequestDto {
    @Email
    private final String principal;

    @NotBlank(message = "must be not blank")
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
