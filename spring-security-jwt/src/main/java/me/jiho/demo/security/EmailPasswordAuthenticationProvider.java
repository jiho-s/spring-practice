package me.jiho.demo.security;

import lombok.RequiredArgsConstructor;
import me.jiho.demo.member.Member;
import me.jiho.demo.member.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

/**
 * @author jiho
 * @since 2021/04/13
 */
@RequiredArgsConstructor
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private final Jwt jwt;

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(EmailPasswordAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("EmailPasswordAuthenticationProvider.onlySupports",
                        "Only JwtAuthenticationToken is supported"
                        ));
        Member member = retrieveMember((EmailPasswordAuthenticationToken) authentication);
        authenticationChecks(member, (EmailPasswordAuthenticationToken) authentication);
        return createSuccessAuthentication(member);
    }

    private void authenticationChecks(Member member, EmailPasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages
                    .getMessage("EmailPasswordAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, member.getPassword())) {
            this.logger.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException(this.messages
                    .getMessage("EmailPasswordAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    private String determineEmail(EmailPasswordAuthenticationToken authentication) {
        return (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
    }

    private Member retrieveMember(EmailPasswordAuthenticationToken authentication) throws AuthenticationException {
        String email = determineEmail(authentication);
        return this.memberService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private Authentication createSuccessAuthentication(Member member) {
        Claims claims = Claims.of(member);
        EmailPasswordAuthenticationToken result = new EmailPasswordAuthenticationToken(
                JwtPrincipal.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .name(member.getName())
                        .build(),
                null,
                createAuthorityList(claims.getRoles()));
        String token = jwt.generateToken(claims);
        result.setDetails(EmailPasswordAuthenticationDetail.builder()
                .token(token)
                .member(member)
                .build()
        );
        this.logger.debug("Authenticated member");
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
