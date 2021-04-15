package me.jiho.demo.configure;

import me.jiho.demo.security.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiho
 * @since 2021/02/03
 */
@Configuration
public class ServiceConfigure {

    @Bean
    public Jwt jwt(JwtTokenProperty jwtTokenProperty) {
        return new Jwt(jwtTokenProperty);
    }
}
