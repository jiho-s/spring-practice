package com.github.prgrms.socialserver.domain;

import java.time.LocalDateTime;

/**
 * @author jiho
 * @since 2021/01/08
 */
public class User {
    private final Long seq;

    private final String email;

    private String passwd;
    private Integer loginCount;
    private LocalDateTime lastLoginAt;
    private final LocalDateTime createAt;

    public User(Long seq, String email, String passwd, Integer loginCount, LocalDateTime lastLoginAt, LocalDateTime createAt) {
        this.seq = seq;
        this.email = email;
        this.passwd = passwd;
        this.loginCount = loginCount;
        this.lastLoginAt = lastLoginAt;
        this.createAt = createAt;
    }

    public Long getSeq() {
        return seq;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswd() {
        return passwd;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
