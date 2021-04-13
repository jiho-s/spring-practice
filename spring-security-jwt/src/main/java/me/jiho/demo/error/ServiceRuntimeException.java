package me.jiho.demo.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jiho
 * @since 2021/04/13
 */
@Getter
@RequiredArgsConstructor
public abstract class ServiceRuntimeException extends RuntimeException {

    private final String messageKey;

    private final String detailKey;

    private final Object[] params;

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
