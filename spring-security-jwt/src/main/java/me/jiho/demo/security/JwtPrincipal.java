package me.jiho.demo.security;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author jiho
 * @since 2021/04/14
 */
@Getter
public class JwtPrincipal {
    private final Long id;

    private final String name;

    private final String email;

    @Builder
    public JwtPrincipal(Long id, String name, String email) {
        Assert.notNull(id, "id must be provided.");
        Assert.notNull(name, "name must be provided.");
        Assert.notNull(email, "email must be provided.");
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtPrincipal that = (JwtPrincipal) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
