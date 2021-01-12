package com.github.prgrms.socialserver.service.exception;

/**
 * @author jiho
 * @since 2021/01/09
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super(email);
    }
}
