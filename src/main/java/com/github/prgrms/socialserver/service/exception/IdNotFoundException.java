package com.github.prgrms.socialserver.service.exception;

/**
 * @author jiho
 * @since 2021/01/10
 */
public class IdNotFoundException extends RuntimeException {
    public IdNotFoundException(String s) {
        super(s);
    }
}
