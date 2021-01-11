package com.github.prgrms.socialserver.domain;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC-based implementation of the {@link UserRepository}
 *
 * @author jiho
 * @since 2021/01/08
 */
@Repository
public class JdbcUserRepositoryImpl implements UserRepository{

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final RowMapper<User> userRowMapper = BeanPropertyRowMapper.newInstance(User.class);

    public JdbcUserRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("users")
                .usingGeneratedKeyColumns("seq");
    }


    @Override
    public User save(User user) throws DataAccessException {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("seq", user.getSeq())
                .addValue("email", user.getEmail())
                .addValue("passwd", user.getPasswd())
                .addValue("login_count", user.getLoginCount())
                .addValue("last_login_at", user.getLastLoginAt());
        if (user.getSeq() == null) {
            Number newSeq = insertUser.executeAndReturnKey(parameters);
            user = User.toSequencedUser(newSeq.longValue(), user);
        } else {
            this.namedParameterJdbcTemplate.update(
                    "UPDATE users SET email=:email, passwd=:passwd, login_count=:login_count, " +
                            "last_login_at=:last_login_at WHERE seq=:seq",
                    parameters);
        }
        return user;
    }

    @Override
    public Collection<User> findAll() throws DataAccessException {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM users",
                userRowMapper
                );
    }

    @Override
    public Optional<User> findById(Long id) throws DataAccessException {
        Map<String, Long> params = Map.of("seq", id);
        User user = this.namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE seq=:seq",
                params,
                userRowMapper
        );
        return Optional.ofNullable(user);
    }

    @Override
    public boolean existByEmail(String email) throws DataAccessException {
        Map<String, String> params = Map.of("email", email);
         int count = this.namedParameterJdbcTemplate.queryForObject(
                "SELECT count(*) FROM users WHERE email=:email",
                params,
                Integer.class
        );
         if (count == 0) {
             return false;
         }
         return true;
    }

}
