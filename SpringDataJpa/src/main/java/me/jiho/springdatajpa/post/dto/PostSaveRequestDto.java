package me.jiho.springdatajpa.post.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import me.jiho.springdatajpa.post.Post;
import me.jiho.springdatajpa.user.User;

/**
 * @author jiho
 * @since 2021/01/27
 */
@RequiredArgsConstructor(staticName = "of")
public class PostSaveRequestDto {
    private final String text;

    public Post toEntity(User user) {
        return Post.builder()
                .text(this.text)
                .user(user)
                .build();
    }

}
