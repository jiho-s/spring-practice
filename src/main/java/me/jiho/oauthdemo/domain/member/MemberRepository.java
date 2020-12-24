package me.jiho.oauthdemo.domain.member;

import me.jiho.oauthdemo.domain.common.BaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface MemberRepository extends BaseRepository<Member> {

    @EntityGraph(attributePaths = "role")
    Optional<Member> findByEmail(String email);
}
