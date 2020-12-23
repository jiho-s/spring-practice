package me.jiho.oauthdemo.domain.common;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository<T> extends JpaRepository<T, Long> {
}
