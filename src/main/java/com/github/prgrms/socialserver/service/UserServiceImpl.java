package com.github.prgrms.socialserver.service;

import com.github.prgrms.socialserver.domain.User;
import com.github.prgrms.socialserver.domain.UserRepository;
import com.github.prgrms.socialserver.rest.dto.UserRequestDto;
import com.github.prgrms.socialserver.service.dto.UserResponseDto;
import com.github.prgrms.socialserver.service.exception.DuplicateEmailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiho
 * @since 2021/01/09
 */
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<UserResponseDto> findAllUser() {
        return null;
    }

    @Override
    public UserResponseDto findUserById(Long id) {
        return null;
    }

    @Override
    public void saveUser(UserRequestDto userRequestDto) {
        if (!userRepository.existByEmail(userRequestDto.getPrincipal())) {
            throw new DuplicateEmailException(userRequestDto.getPrincipal());
        }
        userRepository.save(new User(
                userRequestDto.getPrincipal(),
                userRequestDto.getCredentials(),
                LocalDateTime.now()));
    }
}
