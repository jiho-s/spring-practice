package me.jiho.demo.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jiho
 * @since 2021/02/03
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtTokenProperty {

    private String header;

    private String issuer;

    private String clientSecret;

    private Integer expirySeconds;

    private Long refreshRangeMills;
}
