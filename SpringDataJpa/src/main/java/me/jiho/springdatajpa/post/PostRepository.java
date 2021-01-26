package me.jiho.springdatajpa.post;

import me.jiho.springdatajpa.common.BaseJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author jiho
 * @since 2021/01/25
 */
public interface PostRepository extends BaseJpaRepository<Post> {

    @Query(value =
            "SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.user.id=:userId")
    Slice<Post> findSliceWithUserByUserId(@Param("userId") Long userId, Pageable pageable);
}
