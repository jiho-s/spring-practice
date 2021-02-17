package me.jiho.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import me.jiho.demo.configure.JwtTokenProperty;
import me.jiho.demo.member.Member;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author jiho
 * @since 2021/02/03
 */
@Component
public class Jwt {

    private final String issuer;

    private final String clientSecret;

    private final int expirySeconds;

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier;

    public Jwt(JwtTokenProperty jwtTokenProperty) {
        this.issuer = jwtTokenProperty.getIssuer();
        this.clientSecret = jwtTokenProperty.getClientSecret();
        this.expirySeconds = jwtTokenProperty.getExpirySeconds();
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    public String generateToken(Member member) {
        Date now = new Date();
        JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withClaim("id", member.getId())
                .withClaim("name", member.getName())
                .with
    }
}
