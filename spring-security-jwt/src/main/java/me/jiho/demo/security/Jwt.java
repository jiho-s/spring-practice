package me.jiho.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import me.jiho.demo.configure.JwtTokenProperty;
import me.jiho.demo.member.Member;
import me.jiho.demo.member.MemberRole;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

/**
 * @author jiho
 * @since 2021/02/03
 */
@Component
public class Jwt {

    private final String issuer;

    private final String clientSecret;

    private final int expirySeconds;

    private final long refreshRangeMills;

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier;


    public Jwt(JwtTokenProperty jwtTokenProperty) {
        this.issuer = jwtTokenProperty.getIssuer();
        this.clientSecret = jwtTokenProperty.getClientSecret();
        this.expirySeconds = jwtTokenProperty.getExpirySeconds();
        this.refreshRangeMills = jwtTokenProperty.getRefreshRangeMills();
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    public String generateToken(Claims claims) {
        Date now = new Date();
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(expirySeconds > 0 ? new Date(now.getTime() + expirySeconds * 1_000L) : null)
                .withClaim("id", claims.getId())
                .withArrayClaim("roles", claims.getRoles())
                .sign(algorithm);
    }

    public String refreshToken(Claims claims) throws JWTVerificationException {
        claims.eraseDates();
        return generateToken(claims);
    }

    public boolean canRefresh(Claims claims) {
        Date exp = claims.getExp();
        if (exp == null) {
            return false;
        }
        long remain = exp.getTime() - System.currentTimeMillis();
        return remain < refreshRangeMills;
    }

    public Claims verify(String token) throws JWTVerificationException {
        return new Claims(jwtVerifier.verify(token));
    }
}
