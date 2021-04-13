package me.jiho.demo.error;

import me.jiho.demo.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author jiho
 * @since 2021/04/13
 */
public class NotFoundException extends ServiceRuntimeException {
    private static final String MESSAGE_KEY = "error.notfound";

    private static final String MESSAGE_DETAILS = "error.notfound.details";

    public NotFoundException(Class<?> cls, Object... values) {
        this(cls.getSimpleName(), values);
    }

    public NotFoundException(String targetName, Object... values) {
        super(MESSAGE_KEY, MESSAGE_DETAILS, new String[]{targetName, (values != null && values.length > 0) ? StringUtils.join(values) : ""});
    }
}
