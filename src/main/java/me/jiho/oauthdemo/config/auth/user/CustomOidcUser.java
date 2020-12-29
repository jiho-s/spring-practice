package me.jiho.oauthdemo.config.auth.user;

import lombok.Builder;
import lombok.Getter;
import me.jiho.oauthdemo.domain.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

@Getter
public class CustomOidcUser extends DefaultOidcUser {
    private Long id;

    @Builder
    public CustomOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, String nameAttributeKey, Long id) {
        super(authorities, idToken, nameAttributeKey);
        this.id = id;
    }
}
