package me.jiho.springdatajpa.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author jiho
 * @since 2021/01/27
 */
@Getter
public class BaseTimeDto extends BaseDto {

    private final LocalDateTime createdTime;

    private final LocalDateTime modifiedTime;

    public BaseTimeDto(Long id, LocalDateTime createdTime, LocalDateTime modifiedTime) {
        super(id);
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }
}
