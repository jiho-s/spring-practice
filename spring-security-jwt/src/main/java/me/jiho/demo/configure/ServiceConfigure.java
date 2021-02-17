package me.jiho.demo.configure;

import com.auth0.jwt.JWT;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiho
 * @since 2021/02/03
 */
@Configuration
public class ServiceConfigure {

    @Bean
    public JWT jwt(JwtTokenProperty jwtTokenProperty) {
        return new JWT()
    }
}
