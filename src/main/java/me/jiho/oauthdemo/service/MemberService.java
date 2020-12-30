package me.jiho.oauthdemo.service;

import lombok.RequiredArgsConstructor;
import me.jiho.oauthdemo.controller.dto.MemberSaveRequestDto;
import me.jiho.oauthdemo.domain.member.Member;
import me.jiho.oauthdemo.domain.member.MemberRepository;
import me.jiho.oauthdemo.service.exception.DuplicateUsernameException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author jiho
 * @since 2020/12/30
 */
@RequiredArgsConstructor
@Service
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public Long save(MemberSaveRequestDto memberSaveRequestDto) {
        if (memberRepository.existsByEmail(memberSaveRequestDto.getEmail())) {
            throw new DuplicateUsernameException(memberSaveRequestDto.getEmail());
        }
        Member member = memberRepository.save(memberSaveRequestDto.toEntity(passwordEncoder));

        return member.getId();
    }
}
