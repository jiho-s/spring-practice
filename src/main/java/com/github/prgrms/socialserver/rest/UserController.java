package com.github.prgrms.socialserver.rest;

import com.github.prgrms.socialserver.rest.dto.UserRequestDto;
import com.github.prgrms.socialserver.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author jiho
 * @since 2021/01/09
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public ResponseEntity createUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        return ResponseEntity.ok(true);
    }
}
