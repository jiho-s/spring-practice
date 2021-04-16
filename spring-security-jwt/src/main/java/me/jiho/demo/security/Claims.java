package me.jiho.demo.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.*;
import me.jiho.demo.member.Member;
import me.jiho.demo.member.MemberRole;

import java.util.Date;
import java.util.Set;

/**
 * @author jiho
 * @since 2021/04/13
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Claims {
    private final Long id;
    private final String [] roles;
    private Date iat;
    private Date exp;

    public Claims(DecodedJWT decodedJWT) {
        Claim id = decodedJWT.getClaim("id");
        this.id = id.isNull() ? null : id.asLong();
        Claim roles = decodedJWT.getClaim("roles");
        this.roles = roles.isNull() ? null : roles.asArray(String.class);
        this.iat = decodedJWT.getIssuedAt();
        this.exp = decodedJWT.getExpiresAt();
    }

    public static Claims of(Member member) {
        return new Claims(member.getId(), memberRolesToStringArray(member.getRoles()));
    }

    private static String [] memberRolesToStringArray(Set<MemberRole> roles) {
        return roles.stream().map(MemberRole::getKey).toArray(String[]::new);
    }

    public void eraseDates() {
        this.iat = null;
        this.exp = null;
    }


}
