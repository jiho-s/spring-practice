package me.jiho.demo.security;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiho
 * @since 2021/04/16
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean
        implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    public static final String DEFAULT_HEADER_KEY = "Authorization";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Pattern BEARER = Pattern.compile("^Bearer\\s?(.+)$", Pattern.CASE_INSENSITIVE);

    private final AuthenticationManager authenticationManager;

    private String headerKey;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    @Builder
    public JwtAuthenticationFilter(ApplicationEventPublisher eventPublisher, AuthenticationManager authenticationManager, String headerKey, AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.eventPublisher = eventPublisher;
        this.authenticationManager = authenticationManager;
        this.headerKey = headerKey != null ? headerKey : DEFAULT_HEADER_KEY;
        this.authenticationDetailsSource = authenticationDetailsSource != null ? authenticationDetailsSource : new WebAuthenticationDetailsSource();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doAuthentication((HttpServletRequest) request, (HttpServletResponse) response);
        chain.doFilter(request, response);
    }

    private void doAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.debug("SecurityContextHolder not populated with security token, as it already contained: '{}'",
                    SecurityContextHolder.getContext().getAuthentication());
            return;
        }
        Authentication authenticationResult = attemptAuthentication(request, response);
        if (authenticationResult == null) {
            return;
        }
        successfulAuthentication(request, response, authenticationResult);
    }

    protected Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String authorizationToken = obtainAuthorizationToken(request);
        if (authorizationToken == null) {
            return null;
        }
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(null, authorizationToken);
        setDetails(request, jwtAuthenticationToken);
        return this.authenticationManager.authenticate(jwtAuthenticationToken);
    }

    private String obtainAuthorizationToken(HttpServletRequest request) {
        String token = request.getHeader(headerKey);
        if (token != null) {
            if (this.logger.isDebugEnabled()) {
                logger.debug("Jwt authorization api detected: {}", token);
            }
            token = URLDecoder.decode(token, StandardCharsets.UTF_8);
            Matcher matcher = BEARER.matcher(token);
            return matcher.find() ? matcher.group(1) : null;
        }
        return null;
    }




    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                          Authentication authResult) {
        if (authResult.getCredentials() != null) {
            // credential이 null이 아니면 refresh token이므로 헤더에 넣어준다.
            setRefreshToken(response, (JwtAuthenticationToken) authResult);
        }
        SecurityContextHolder.getContext().setAuthentication(authResult);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Set SecurityContextHolder to {}", authResult);
        }
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }

    }

    private void setRefreshToken(HttpServletResponse response, JwtAuthenticationToken authResult) {
        response.setHeader(headerKey, authResult.getCredentials().toString());
        authResult.eraseCredentials();
    }

    private void setDetails(HttpServletRequest request, JwtAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

    }
}
