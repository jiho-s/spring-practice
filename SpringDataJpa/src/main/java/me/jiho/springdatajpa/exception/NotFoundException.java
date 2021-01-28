package me.jiho.springdatajpa.exception;

import java.util.List;

/**
 * @author jiho
 * @since 2021/01/27
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
