package me.jiho.springdatajpa.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class Post extends BaseTimeEntity {

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    public void modify(String text) {
        this.text = text;
    }
}
