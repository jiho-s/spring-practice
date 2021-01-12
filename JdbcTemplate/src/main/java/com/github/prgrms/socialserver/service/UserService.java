package com.github.prgrms.socialserver.service;

import com.github.prgrms.socialserver.domain.User;
import com.github.prgrms.socialserver.domain.UserRepository;
import com.github.prgrms.socialserver.rest.dto.UserRequestDto;
import com.github.prgrms.socialserver.service.dto.UserResponseDto;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author jiho
 * @since 2021/01/09
 */
public interface UserService {
    Collection<User> findAllUser();
    Optional<User> findUserById(Long id);
    User saveUser(User user);
}
