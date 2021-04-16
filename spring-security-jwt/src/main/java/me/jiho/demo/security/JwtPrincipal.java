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


    @Builder
    public JwtPrincipal(Long id) {
        Assert.notNull(id, "id must be provided.");
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtPrincipal that = (JwtPrincipal) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
