package me.jiho.demo.member;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jiho
 * @since 2021/04/16
 */
@Getter
@RequiredArgsConstructor
public class MemberDto {

    private final Long id;

    private final String email;

    private final String name;

    public static MemberDto of(Member member) {
        return new MemberDto(member.getId(), member.getEmail(), member.getName());
    }
}
