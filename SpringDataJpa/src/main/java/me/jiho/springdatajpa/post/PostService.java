package me.jiho.springdatajpa.post;

import lombok.RequiredArgsConstructor;
import me.jiho.springdatajpa.comment.Comment;
import me.jiho.springdatajpa.comment.CommentRepository;
import me.jiho.springdatajpa.exception.NotFoundException;
import me.jiho.springdatajpa.post.dto.PostSaveRequestDto;
import me.jiho.springdatajpa.post.dto.PostResponseDto;
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
