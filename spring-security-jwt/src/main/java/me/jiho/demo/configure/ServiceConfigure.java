package me.jiho.demo.configure;

import me.jiho.demo.member.Member;
import me.jiho.demo.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author jiho
 * @since 2021/02/03
 */
@Configuration
public class ServiceConfigure {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            MemberRepository memberRepository;

            @Autowired
            PasswordEncoder passwordEncoder;
            @Override
            public void run(ApplicationArguments args) throws Exception {
                Member jiho = memberRepository.save(Member.builder()
                        .email("test@email.com")
                        .password(passwordEncoder.encode("1234"))
                        .name("jiho")
                        .build());
                System.out.println(jiho.getEmail());
            }
        };
    }
}
