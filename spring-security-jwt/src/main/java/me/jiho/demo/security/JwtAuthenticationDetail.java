package me.jiho.demo.security;

import lombok.Builder;
import lombok.Getter;
import me.jiho.demo.member.Member;
import org.springframework.util.Assert;

/**
 * @author jiho
 * @since 2021/04/14
 */
@Getter
public class JwtAuthenticationDetail {

    private final String token;

    private final Member member;

    @Builder
    public JwtAuthenticationDetail(String token, Member member) {
        Assert.notNull(token, "apiToken must be provided.");
        Assert.notNull(member, "member must be provided.");
        this.token = token;
        this.member = member;
    }
}
