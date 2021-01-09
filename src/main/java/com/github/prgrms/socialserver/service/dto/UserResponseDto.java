package com.github.prgrms.socialserver.service.dto;

import java.time.LocalDateTime;

/**
 * @author jiho
 * @since 2021/01/09
 */
public class UserResponseDto {
    private final Long seq;
    private final String email;
    private final Integer login_count;
    private final LocalDateTime last_login_at;
    private final LocalDateTime create_at;

    public UserResponseDto(Long seq, String email, Integer login_count, LocalDateTime last_login_at, LocalDateTime create_at) {
        this.seq = seq;
        this.email = email;
        this.login_count = login_count;
        this.last_login_at = last_login_at;
        this.create_at = create_at;
    }

    public Long getSeq() {
        return seq;
    }

    public String getEmail() {
        return email;
    }

    public Integer getLogin_count() {
        return login_count;
    }

    public LocalDateTime getLast_login_at() {
        return last_login_at;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }
}
