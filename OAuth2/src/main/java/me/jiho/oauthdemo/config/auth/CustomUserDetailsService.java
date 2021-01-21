package me.jiho.oauthdemo.config.auth;

import me.jiho.oauthdemo.config.auth.user.CustomUser;
import me.jiho.oauthdemo.domain.member.Member;
import me.jiho.oauthdemo.domain.member.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author jiho
 * @since 2020/12/30
 */
@Service
public class CustomUserDetailsService extends BaseUserService  implements UserDetailsService {

    public CustomUserDetailsService(MemberRepository memberRepository) {
        super(memberRepository);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail("{base}" + s).orElseThrow(() -> new UsernameNotFoundException(s));
        return CustomUser.builder()
                .member(member)
                .authorities(authorities(member.getRole()))
                .build();
    }
}
