package me.jiho.demo.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.jiho.demo.member.MemberService;
import me.jiho.demo.security.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author jiho
 * @since 2021/04/15
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final Jwt jwt;

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper;

    private final JwtTokenProperty jwtTokenProperty;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger-resources", "/webjars/**", "/static/**", "/templates/**", "/h2/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new EmailPasswordAuthenticationProvider(jwt, memberService, passwordEncoder));
        auth.authenticationProvider(new JwtAuthenticationProvider(jwt));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()
                .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .exceptionHandling()
                    .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper))
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))
                    .and()
                .formLogin()
                    .disable();
        EmailPasswordAuthenticationFilter authFilter = new EmailPasswordAuthenticationFilter(this.authenticationManager());
        authFilter.setAuthenticationFailureHandler(new EmailPasswordAuthenticationFailureHandler(objectMapper));
        authFilter.setAuthenticationSuccessHandler(new EmailPasswordAuthenticationSuccessHandler(objectMapper));
        JwtAuthenticationFilter jwtAuthenticationFilter = JwtAuthenticationFilter.builder()
                .authenticationManager(this.authenticationManager())
                .headerKey(jwtTokenProperty.getHeader())
                .build();
        http
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, authFilter.getClass());


    }
}
