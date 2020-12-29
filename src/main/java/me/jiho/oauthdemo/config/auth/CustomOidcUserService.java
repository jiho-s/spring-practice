package me.jiho.oauthdemo.config.auth;

import me.jiho.oauthdemo.config.auth.dto.OAuthAttributes;
import me.jiho.oauthdemo.config.auth.user.CustomOidcUser;
import me.jiho.oauthdemo.domain.member.Member;
import me.jiho.oauthdemo.domain.member.MemberRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOidcUserService extends BaseOAuth2UserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    public CustomOidcUserService(MemberRepository memberRepository) {
        super(memberRepository);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oidcUser.getAttributes());

        Member member = loadOrCreate(attributes);

        return CustomOidcUser.builder()
                .id(member.getId())
                .authorities(authorities(member.getRole()))
                .idToken(userRequest.getIdToken())
                .nameAttributeKey(attributes.getNameAttributeKey())
                .build();
    }
}
