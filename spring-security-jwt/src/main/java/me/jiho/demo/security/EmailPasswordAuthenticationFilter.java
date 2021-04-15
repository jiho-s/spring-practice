package me.jiho.demo.security;

import lombok.Getter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jiho
 * @since 2021/04/15
 */
public class EmailPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String EMAIL_KEY = "email";

    public static final String PASSWORD_KEY = "password";

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/v1/login",
            "POST");

    @Getter
    private String emailParameter = EMAIL_KEY;

    @Getter
    private String passwordParameter = PASSWORD_KEY;

    private boolean postOnly = true;

    public EmailPasswordAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public EmailPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String email = obtainEmail(request);
        email = (email != null) ? email : "";
        String password = obtainPassword(request);
        password = (password != null) ? password : "";
        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(email, password);
//        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainEmail(HttpServletRequest request) {
        return request.getParameter(this.emailParameter);
    }

    private String obtainPassword(HttpServletRequest request) {
        return request.getParameter(this.passwordParameter);
    }

    private void setDetails(HttpServletRequest request, JwtAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setEmailParameter(String emailParameter) {
        Assert.hasText(emailParameter, "Email parameter must not be empty or null");
        this.emailParameter = emailParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }


}
