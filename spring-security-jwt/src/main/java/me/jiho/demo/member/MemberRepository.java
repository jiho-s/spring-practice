package me.jiho.demo.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author jiho
 * @since 2021/02/03
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Override
    @EntityGraph(attributePaths = "roles")
    Optional<Member> findById(Long aLong);

    @EntityGraph(attributePaths = "roles")
    Optional<Member> findByEmail(String email);
}
