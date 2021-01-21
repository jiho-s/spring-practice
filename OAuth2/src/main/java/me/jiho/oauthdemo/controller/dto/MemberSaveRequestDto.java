package me.jiho.oauthdemo.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.jiho.oauthdemo.domain.member.Member;
import me.jiho.oauthdemo.domain.member.MemberRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

/**
 * @author jiho
 * @since 2020/12/30
 */
@Getter
@NoArgsConstructor
public class MemberSaveRequestDto {
    @Email
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String password;

    @Builder
    public MemberSaveRequestDto(@Email String email, @NotBlank String name, @NotBlank String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email("{base}" + email)
                .name(name)
                .role(Collections.singleton(MemberRole.USER))
                .password(passwordEncoder.encode(password))
                .build();
    }
}
