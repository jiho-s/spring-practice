package com.github.prgrms.socialserver.rest;

import com.github.prgrms.socialserver.rest.dto.UserCreateResponseDto;
import com.github.prgrms.socialserver.rest.dto.UserRequestDto;
import com.github.prgrms.socialserver.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserCreateResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(new UserCreateResponseDto(
                false,
                errors
        ));
    }
}
