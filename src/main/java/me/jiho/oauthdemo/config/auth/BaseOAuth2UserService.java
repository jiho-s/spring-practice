package me.jiho.oauthdemo.config.auth;

import lombok.RequiredArgsConstructor;
import me.jiho.oauthdemo.config.auth.dto.OAuthAttributes;
import me.jiho.oauthdemo.domain.member.Member;
import me.jiho.oauthdemo.domain.member.MemberRepository;
import me.jiho.oauthdemo.domain.member.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class BaseOAuth2UserService {
    private final MemberRepository memberRepository;

    protected Member loadOrCreate(OAuthAttributes attributes) {
        return memberRepository.findByEmail(attributes.getEmail())
                .orElseGet(() ->memberRepository.save(attributes.toMember()));
    }

    protected static Collection<? extends GrantedAuthority> authorities(Set<MemberRole> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.getKey()))
                .collect(Collectors.toSet());
    }
}
