package me.jiho.demo.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jiho
 * @since 2021/02/03
 */
@Getter
@RequiredArgsConstructor
public enum MemberRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String key;
}
