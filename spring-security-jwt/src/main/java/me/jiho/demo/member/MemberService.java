package me.jiho.demo.member;

import lombok.RequiredArgsConstructor;
import me.jiho.demo.error.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * @author jiho
 * @since 2021/02/03
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Optional<Member> findById(Long memberId) {
        Assert.notNull(memberId, "memberId must be provided.");
        return memberRepository.findById(memberId);
    }

    public Optional<Member> findByEmail(String email) {
        Assert.notNull(email, "email must be provided");
        return memberRepository.findByEmail(email);
    }


}
