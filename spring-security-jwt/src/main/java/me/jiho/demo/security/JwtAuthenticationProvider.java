package me.jiho.demo.security;

import lombok.RequiredArgsConstructor;
import me.jiho.demo.member.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

/**
 * @author jiho
 * @since 2021/04/16
 */
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private final Jwt jwt;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("JwtAuthenticationProvider.onlySupports",
                        "Only JwtAuthenticationToken is supported"
                ));
        Claims claims = retrieveClaims((JwtAuthenticationToken) authentication);
        if (claims == null) {
            return null;
        }
        return createSuccessAuthentication(claims, authentication);
    }

    private Claims retrieveClaims(JwtAuthenticationToken authentication) {
        String token = authentication.getCredentials().toString();
        try {
            Claims claims = verify(token);
            logger.debug("Jwt parse result: {}", claims);
            return claims;
        } catch (Exception e) {
            logger.warn("Jwt processing failed: {}", e.getMessage());
        }
        return null;
    }

    private Authentication createSuccessAuthentication(Claims claims, Authentication authentication) {
        String refreshedToken = null;
        if (canRefresh(claims)) {
            refreshedToken = refreshToken(claims);
        }
        JwtAuthenticationToken result = new JwtAuthenticationToken(
                JwtPrincipal.builder()
                        .id(claims.getId())
                        .build(),
                refreshedToken,
                createAuthorityList(claims.getRoles()));
        result.setDetails(authentication.getDetails());
        this.logger.debug("Authenticated token");
        return result;
    }

    private Claims verify(String token) {
        return jwt.verify(token);
    }

    private boolean canRefresh(Claims claims) {
        return this.jwt.canRefresh(claims);
    }

    private String refreshToken(Claims claims) {
        return  jwt.refreshToken(claims);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
