package me.jiho.oauthdemo.controller;

import me.jiho.oauthdemo.config.auth.CurrentUserId;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
public class SocialApplication {

    @GetMapping("/user")
    public Map<String, Object> user(@CurrentUserId Long principal) {
//        return Collections.singletonMap("name", principal.getAttribute("login"));
        return Collections.singletonMap("name", String.valueOf(principal));
    }

    @GetMapping("/error")
    public String error(HttpServletRequest request) {
        String message = (String) request.getSession().getAttribute("error.message");
        request.getSession().removeAttribute("error.message");
        return message;
    }
}
