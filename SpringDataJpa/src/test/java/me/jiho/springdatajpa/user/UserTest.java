package me.jiho.springdatajpa.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jiho
 * @since 2021/01/25
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private User user;

    @BeforeAll
    void setUp() {
        user = User.builder()
                .email("test@email.com")
                .name("test")
                .build();
    }

    @Test
    public void toStringTest() {
        log.warn(user.toString());
    }
}