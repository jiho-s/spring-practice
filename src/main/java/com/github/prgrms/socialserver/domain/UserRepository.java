package com.github.prgrms.socialserver.domain;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

/**
 * @author jiho
 * @since 2021/01/08
 */
public interface UserRepository {

     void save(User user) throws DataAccessException;

     Collection<User> findAll() throws DataAccessException;

     Optional<User> findById(Long id) throws DataAccessException;


}
