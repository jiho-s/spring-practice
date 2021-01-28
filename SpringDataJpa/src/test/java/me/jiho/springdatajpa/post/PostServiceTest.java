package me.jiho.springdatajpa.post;

import me.jiho.springdatajpa.comment.Comment;
import me.jiho.springdatajpa.comment.CommentRepository;
import me.jiho.springdatajpa.post.dto.PostResponseDto;
import me.jiho.springdatajpa.user.User;
import me.jiho.springdatajpa.user.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author jiho
 * @since 2021/01/27
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostServiceTest {

    private static final long USER_ID = 1L;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    private PostService postService;

    @BeforeAll
    void setup() {
        User user = User.builder()
                .name("test")
                .email("test@email.com")
                .build();
        user.setId(USER_ID);
        Post post = Post.builder()
                .text("test1")
                .id(1L)
                .user(user)
                .build();
        List<Post> posts = List.of(
                post,
                Post.builder()
                .text("test2")
                .id(2L)
                .user(user)
                .build()
        );

        postRepository = mock(PostRepository.class);
        given(postRepository.findSliceWithUserByUserId(anyLong(), any())).will(answer -> {
            Long userId = answer.getArgument(0);
            Pageable pageable = answer.getArgument(1);

            if (userId.equals(USER_ID) && pageable.getPageNumber() == 0) {
                return new SliceImpl<Post>(posts, PageRequest.of(0, 2), false);
            }
            return new SliceImpl<Post>(List.of(), pageable, false);
        });
        commentRepository = mock(CommentRepository.class);
        given(commentRepository.findByPostIdList(List.of(1L, 2L))).willReturn(List.of(
                Comment.builder()
                        .id(1L)
                        .author(user)
                        .post(post)
                        .text("comment1")
                        .build(),
                Comment.builder()
                        .id(2L)
                        .post(post)
                        .author(user)
                        .text("comment2")
                        .build()
        ));


        postService = new PostService(userRepository, postRepository, commentRepository);
    }

    @Test
    @DisplayName("포스트 조회")
    public void queryPost() {
        Slice<PostResponseDto> sliceByUserId = postService.findSliceByUserId(USER_ID, PageRequest.of(0, 2));
        assertThat(sliceByUserId, is(notNullValue()));
        List<PostResponseDto> content = sliceByUserId.getContent();
        assertThat(content, is(notNullValue()));
        assertThat(content.get(0).getComments(), is(notNullValue()));
    }

}