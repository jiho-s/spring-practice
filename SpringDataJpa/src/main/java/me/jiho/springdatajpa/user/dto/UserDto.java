package me.jiho.springdatajpa.user.dto;

import me.jiho.springdatajpa.common.BaseEntity;
import me.jiho.springdatajpa.user.User;

/**
 * @author jiho
 * @since 2021/01/27
 */
public class UserDto extends BaseEntity {
    private final String name;

    public UserDto(User user) {
        super(user.getId());
        this.name = user.getName();
    }

    public static UserDto toDto(User user) {
        return new UserDto(user);
    }
}
