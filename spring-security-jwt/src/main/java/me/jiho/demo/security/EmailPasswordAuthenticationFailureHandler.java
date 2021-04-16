package me.jiho.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.jiho.demo.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jiho
 * @since 2021/04/15
 */
@RequiredArgsConstructor
public class EmailPasswordAuthenticationFailureHandler implements AuthenticationFailureHandler {

    public static ApiResponse<?> CONFLICT_EMAIL = ApiResponse.error("Authentication error (cause: email not found)", HttpStatus.CONFLICT);

    public static ApiResponse<?> DEFAULT_CONFLICT = ApiResponse.error("Authentication error (cause: badCredentials)", HttpStatus.CONFLICT);

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ApiResponse<?> apiResponse = DEFAULT_CONFLICT;

        if (UsernameNotFoundException.class.isAssignableFrom(exception.getClass())) {
            apiResponse = CONFLICT_EMAIL;
        }
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
