package me.jiho.springdatajpa.comment;

import lombok.*;
import me.jiho.springdatajpa.common.BaseTimeEntity;
import me.jiho.springdatajpa.post.Post;
import me.jiho.springdatajpa.user.User;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * @author jiho
 * @since 2021/01/25
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"text"}, callSuper = true)
public class Comment extends BaseTimeEntity {
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder
    public Comment(Long id, String text, User author, Post post) {
        super(id);
        this.text = text;
        this.author = author;
        this.post = post;
        if (post != null) {
            post.getComments().add(this);
        }
        if (author != null) {
            author.getComments().add(this);
        }
    }

    public void modify(String text) {
        this.text = text;
    }
}
