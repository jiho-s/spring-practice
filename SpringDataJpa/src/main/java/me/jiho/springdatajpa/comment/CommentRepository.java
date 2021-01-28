package me.jiho.springdatajpa.comment;

import me.jiho.springdatajpa.common.BaseJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author jiho
 * @since 2021/01/25
 */
public interface CommentRepository extends BaseJpaRepository<Comment> {

    @Query(value = "SELECT c " +
            "FROM Comment c " +
            "JOIN FETCH c.post " +
            "JOIN FETCH c.author " +
            "WHERE c.post.id IN :postIds")
    List<Comment> findByPostIdList(@Param("postIds") List<Long> postIds);

    @Query(value = "SELECT c " +
            "FROM Comment c " +
            "JOIN FETCH c.author " +
            "WHERE c.post.id=:postId")
    Slice<Comment> findSliceByPostId(@Param("postId") Long postId, Pageable pageable);

}
