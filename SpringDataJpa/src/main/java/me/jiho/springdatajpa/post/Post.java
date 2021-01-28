package me.jiho.springdatajpa.post;

import lombok.*;
import me.jiho.springdatajpa.comment.Comment;
import me.jiho.springdatajpa.common.BaseTimeEntity;
import me.jiho.springdatajpa.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiho
 * @since 2021/01/25
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"text"}, callSuper = true)
public class Post extends BaseTimeEntity {

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    public void modify(String text) {
        this.text = text;
    }

    @Builder
    public Post(Long id, String text, User user) {
        super(id);
        this.text = text;
        this.user = user;
    }
}
