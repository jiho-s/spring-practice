package com.github.prgrms.socialserver.domain;

import com.github.prgrms.socialserver.service.dto.UserResponseDto;
import org.springframework.dao.DataAccessException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author jiho
 * @since 2021/01/08
 */
public interface UserRepository {

     User save(User user) throws DataAccessException;

     Collection<User> findAll() throws DataAccessException;

     Optional<User> findById(Long id) throws DataAccessException;

     boolean existByEmail(String email) throws DataAccessException;


}
