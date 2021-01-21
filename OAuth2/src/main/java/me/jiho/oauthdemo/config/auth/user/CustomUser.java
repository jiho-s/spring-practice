package me.jiho.oauthdemo.config.auth.user;

import lombok.Builder;
import me.jiho.oauthdemo.domain.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author jiho
 * @since 2020/12/30
 */
public class CustomUser extends User {
    private Long id;

    @Builder
    public CustomUser(Member member, Collection<? extends GrantedAuthority> authorities) {
        super(member.getEmail(), member.getPassword(), authorities);
        this.id = member.getId();
    }
}
