package com.github.prgrms.socialserver.service;

import com.github.prgrms.socialserver.domain.UserRepository;
import com.github.prgrms.socialserver.rest.dto.UserRequestDto;
import com.github.prgrms.socialserver.service.dto.UserResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jiho
 * @since 2021/01/09
 */
public interface UserService {
    List<UserResponseDto> findAllUser();
    UserResponseDto findUserById(Long id);
    void saveUser(UserRequestDto userRequestDto);
}
