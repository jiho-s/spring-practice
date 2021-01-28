package me.jiho.springdatajpa.comment.dto;

import lombok.Builder;
import me.jiho.springdatajpa.comment.Comment;
import me.jiho.springdatajpa.common.BaseTimeDto;
import me.jiho.springdatajpa.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * @author jiho
 * @since 2021/01/27
 */
public class CommentDto extends BaseTimeDto {

    private final String text;

    private final UserDto author;

    @Builder
    public CommentDto(Comment comment) {
        super(comment.getId(), comment.getCreatedDate(), comment.getModifiedDate());
        this.text = comment.getText();
        this.author = new UserDto(comment.getAuthor());
    }
}
