package me.jiho.springdatajpa.post.dto;

import lombok.Builder;
import lombok.Getter;
import me.jiho.springdatajpa.comment.Comment;
import me.jiho.springdatajpa.comment.dto.CommentDto;
import me.jiho.springdatajpa.common.BaseTimeDto;
import me.jiho.springdatajpa.post.Post;
import me.jiho.springdatajpa.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author jiho
 * @since 2021/01/27
 */
@Getter
public class PostResponseDto extends BaseTimeDto {

    private final String text;

    private final Integer commentsCount;

    private final List<CommentDto> comments;

    private final UserDto author;

    @Builder
    public PostResponseDto(Post post, Integer commentsCount, List<Comment> comments) {
        super(post.getId(), post.getCreatedDate(), post.getModifiedDate());
        this.text = post.getText();
        this.commentsCount = commentsCount;
        this.comments = comments.stream().map(CommentDto::new).limit(3).collect(toList());
        this.author = new UserDto(post.getUser());
    }

    public static PostResponseDto of(Post post) {
        return new PostResponseDto(post, 0, List.of());
    }
}
