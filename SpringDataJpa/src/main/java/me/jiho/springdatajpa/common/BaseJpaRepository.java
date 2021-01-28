package me.jiho.springdatajpa.common;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jiho
 * @since 2021/01/25
 */
public interface BaseJpaRepository<T> extends JpaRepository<T, Long> {
}
