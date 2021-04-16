package me.jiho.demo.controller;

import lombok.Getter;
import me.jiho.demo.security.JwtPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiho
 * @since 2021/04/16
 */
@RestController
@RequestMapping(value = "test")
public class TestController {
    @GetMapping
    public String test(@AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        return "id: " + jwtPrincipal.getId();
    }
}
