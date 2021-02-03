package me.jiho.demo.member;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jiho
 * @since 2021/02/03
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
}
