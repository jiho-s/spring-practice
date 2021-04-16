package me.jiho.demo.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author jiho
 * @since 2021/04/13
 */

public class StringUtils {

    public static String join(Object [] values) {
        return Arrays.stream(values).map(Object::toString).collect(Collectors.joining());
    }
}
