package com.github.prgrms.socialserver.domain;

import java.time.LocalDateTime;

/**
 * domain object for User
 *
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

    public User(String email, String passwd,LocalDateTime createAt) {
        this.seq = null;
        this.email = email;
        this.passwd = passwd;
        this.loginCount = 0;
        this.createAt = createAt;
    }

    public static User toSequencedUser(Long seq, User user) {
        return new User(
                seq,
                user.getEmail(),
                user.getPasswd(),
                user.getLoginCount(),
                user.getLastLoginAt(),
                user.getCreateAt()
        );
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

    public void addLoginCount() {
        if (loginCount == null) {
            this.loginCount = 1;
        }
        this.loginCount++;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
