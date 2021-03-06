package me.jiho.springdatajpa.user;

import lombok.*;
import me.jiho.springdatajpa.comment.Comment;
import me.jiho.springdatajpa.common.BaseTimeEntity;
import me.jiho.springdatajpa.post.Post;

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
@ToString(of = {"email", "name"}, callSuper = true)
public class User extends BaseTimeEntity {

    private String email;

    private String name;


    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @OrderBy("createdDate DESC")
    private final List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @OrderBy("createdDate DESC")
    private final List<Comment> comments = new ArrayList<>();

    @Builder
    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }


}
