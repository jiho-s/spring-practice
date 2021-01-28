# Spring Data JPA 연습

## 목차

1. [Entity](#entity)
2. [Repository](#repository)
3. [Service](#service)

## Entity

### BaseEntity

`id` 를 가지고 있는 추상 클래스

- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`

  JPA 스팩으로 기본 생성자가 있어야 한다.

- `equals()`, `hashCode()`

  entity는 `id`가 같으면 같은 entity이다.

```java
package me.jiho.springdatajpa.common;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author jiho
 * @since 2021/01/25
 */
@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id"})
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    public BaseEntity(Long id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

```

### BaseTimeEntity

생성시간, 수정시간을 표현

- `EntityListeners(AuditingEntityListener.class)`

  해당 Entity에 Auditing 기능을 포함

- `@CreatedDate`, `@LastModifiedDate`

  Entity가 생성 및 수정될 때 자동으로 저장

```java
package me.jiho.springdatajpa.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author jiho
 * @since 2021/01/25
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"createdDate", "modifiedDate"}, callSuper = true)
public abstract class BaseTimeEntity extends BaseEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public BaseTimeEntity(Long id) {
        super(id);
    }
}
```

### User

- `cascade = CascadeType.REMOVE`

  `User` 가 삭제 될때 관련된 `Comment`, `Post` 도 삭제

```java
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
```

### Post

생성시 해당 `User` 에 `posts`에도 추가

```java
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
    @OrderBy(value = "createdDate ASC")
    private List<Comment> comments = new ArrayList<>();

    public void modify(String text) {
        this.text = text;
    }

    @Builder
    public Post(Long id, String text, User user) {
        super(id);
        this.text = text;
        this.user = user;
        if (user != null) {
            user.getPosts().add(this);
        }
    }
}
```

### Comment

```java
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
```

## Repository

### BaseJpaRepository

```java
package me.jiho.springdatajpa.common;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jiho
 * @since 2021/01/25
 */
public interface BaseJpaRepository<T> extends JpaRepository<T, Long> {
}

```

### PostRepository

- `findSliceWithUserByUserId`

  `User`와 fetch join 하여 `Slice` 로 조회

- `findByIdAndUserId`

  `Post` 의 `id` 와 `user.id` 를 이용해 조회한 결과를 `Optional`로 반환

```java
package me.jiho.springdatajpa.post;

import me.jiho.springdatajpa.common.BaseJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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

    @Query(value = "SELECT p " +
            "FROM Post p " +
            "WHERE p.id=:id  AND p.user.id=:userId ")
    Optional<Post> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}

```

### CommentRepository

- `findByPostIdList`

  `post` 의 `id` 리스트로 where in 조회

- `findSliceByPostId`

  post의 id로 `Slice` 조회

```java
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
```

## Service

### PostService

- `findSliceByUserId`

  `userId` 로 `Post` 의 리스트를 조회한다. 해당하는 `id` 의 리스트를 `toPostIds` 를 이용해 만든다. 해당 post에 연결해준다.

```java
package me.jiho.springdatajpa.post;

import lombok.RequiredArgsConstructor;
import me.jiho.springdatajpa.comment.Comment;
import me.jiho.springdatajpa.comment.CommentRepository;
import me.jiho.springdatajpa.exception.NotFoundException;
import me.jiho.springdatajpa.post.dto.PostResponseDto;
import me.jiho.springdatajpa.post.dto.PostSaveRequestDto;
import me.jiho.springdatajpa.post.dto.PostUpdateRequestDto;
import me.jiho.springdatajpa.user.User;
import me.jiho.springdatajpa.user.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author jiho
 * @since 2021/01/27
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Slice<PostResponseDto> findSliceByUserId(Long userId, Pageable pageable) {
        Slice<Post> postSlice = postRepository.findSliceWithUserByUserId(userId, pageable);
        Map<Post, List<Comment>> comments = commentRepository.findByPostIdList(toPostIds(postSlice.getContent())).stream()
                .collect(groupingBy(Comment::getPost));
        return postSlice.map(post -> {
            List<Comment> currentComments = comments.get(post);
            currentComments = currentComments != null ? currentComments : List.of();
            return PostResponseDto.builder()
                    .post(post)
                    .comments(currentComments)
                    .commentsCount(currentComments.size())
                    .build();
        });
    }

    @Transactional
    public PostResponseDto create(PostSaveRequestDto postSaveRequestDto, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException(String.valueOf(currentUserId)));
        Post post = postRepository.save(postSaveRequestDto.toEntity(user));
        return PostResponseDto.of(post);
    }

    @Transactional
    public PostResponseDto update(PostUpdateRequestDto postUpdateRequestDto, Long currentUserId) {
        Post post = postRepository.findByIdAndUserId(postUpdateRequestDto.getId(), currentUserId)
                .orElseThrow(() -> new NotFoundException(String.valueOf(postUpdateRequestDto.getId())));
        post.modify(postUpdateRequestDto.getText());
        post = postRepository.save(post);
        return PostResponseDto.of(post);
    }

    @Transactional
    public void delete(Long postId, Long currentUserId) {
        Post post = postRepository.findByIdAndUserId(postId, currentUserId).orElseThrow(() -> new NotFoundException(String.valueOf(postId)));
        postRepository.delete(post);
    }

    private List<Long> toPostIds(List<Post> posts) {
        return posts.stream().map(Post::getId).collect(toList());
    }
}

```

