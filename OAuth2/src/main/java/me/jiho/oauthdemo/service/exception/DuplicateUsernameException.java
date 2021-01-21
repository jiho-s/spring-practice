package me.jiho.oauthdemo.service.exception;

/**
 * @author jiho
 * @since 2020/12/30
 */
public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}
