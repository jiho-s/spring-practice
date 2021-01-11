package com.github.prgrms.socialserver.rest;

import com.github.prgrms.socialserver.rest.dto.CommonResponseDto;
import com.github.prgrms.socialserver.rest.dto.UserRequestDto;
import com.github.prgrms.socialserver.service.UserService;
import com.github.prgrms.socialserver.service.dto.UserResponseDto;
import com.github.prgrms.socialserver.service.exception.DuplicateEmailException;
import com.github.prgrms.socialserver.service.exception.IdNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author jiho
 * @since 2021/01/09
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final MessageSource messageSource;

    public UserController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @PostMapping("/join")
    public ResponseEntity<CommonResponseDto> createUser(@RequestBody @Valid UserRequestDto userRequestDto, UriComponentsBuilder uriComponentsBuilder) {
        Long userSeq = userService.saveUser(userRequestDto);
        UriComponents uriComponents = uriComponentsBuilder.path("/{id}").buildAndExpand(userSeq);
        return ResponseEntity
                .created(uriComponents.toUri())
                .body(CommonResponseDto
                        .success(messageSource.getMessage("join", null, Locale.KOREA)));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> queryUsers() {
        return ResponseEntity.ok(userService.findAllUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(CommonResponseDto.fail(errors));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<CommonResponseDto<Map<String, String>>> handleDuplicateEmailException(DuplicateEmailException e) {
        Map<String, String> error = Map.of("principal", "duplicate " + e.getMessage());
        return ResponseEntity.badRequest().body(CommonResponseDto.fail(error));
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<CommonResponseDto<Map<String, String>>> handleIdNotFoundException(IdNotFoundException e) {
        Map<String, String> error = Map.of("seq", "not found " + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonResponseDto.fail(error));
    }
}
