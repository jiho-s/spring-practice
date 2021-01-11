package com.github.prgrms.socialserver.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prgrms.socialserver.rest.dto.UserRequestDto;
import com.github.prgrms.socialserver.service.UserService;
import com.github.prgrms.socialserver.service.dto.UserResponseDto;
import com.github.prgrms.socialserver.service.exception.IdNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author jiho
 * @since 2021/01/09
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponseDto user1;

    @BeforeEach
    void setup() {
        user1 = new UserResponseDto(
                1L,
                "test1@email.com",
                0,
                null,
                LocalDateTime.now()
        );
        UserResponseDto user2 = new UserResponseDto(
                2L,
                "test2@email.com",
                0,
                null,
                LocalDateTime.now()
        );
        given(this.userService.findAllUser()).willReturn(List.of(user1, user2));
        given(this.userService.findUserById(anyLong())).will((answer) -> {
            Long argument = answer.getArgument(0);
            if (argument.equals(user1.getSeq())) {
                return user1;
            } else if (argument.equals(user2.getSeq())) {
                return user2;
            }
            throw new IdNotFoundException(String.valueOf(argument));
        });

    }

    @Test
    @DisplayName("User 저장 성공")
    public void testCreateUser_Success() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto("test@email.com", "passwd");
        mockMvc.perform(post("/api/users/join")
                .content(objectMapper.writeValueAsString(userRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("response").value("가입완료"))
                .andDo(print());
    }

    @Test
    @DisplayName("User 저장 실패 이메일 형식이 아닌경우")
    public void testCreateUser_Fail_NotEmailForm() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto("test", "passwd");
        mockMvc.perform(post("/api/users/join")
                .content(objectMapper.writeValueAsString(userRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("response").isNotEmpty())
                .andExpect(jsonPath("response['principal']").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("User 저장 실패 이메일이 비어있는경우")
    public void testCreatUser_Fail_EmptyPrincipal() throws Exception {
        mockMvc.perform(post("/api/users/join")
                .content("\"principal\":null,\"credentials\":\"passwd\"")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("response").isNotEmpty())
                .andExpect(jsonPath("response['principal']").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("User 저장 실패 비밀번호가 비어있는 경우")
    public void testCreateUser_Fail_EmptyCredentials() throws Exception {
        mockMvc.perform(post("/api/users/join")
                .content("\"principal\":\"test@email.com\",\"credentials\":null")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("response").isNotEmpty())
                .andExpect(jsonPath("response['credentials']").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("User 저장 실패 비밀번호가 공백인 경우")
    public void testCreateUser_Fail_BlankCredentials() throws Exception {
        mockMvc.perform(post("/api/users/join")
                .content("\"principal\":\"test@email.com\",\"credentials\":\" \"")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("response").isNotEmpty())
                .andExpect(jsonPath("response['credentials']").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("전체 유저 조회 성공")
    public void testQueryUser_Success() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0]").exists())
                .andExpect(jsonPath("[1]").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("유저 아이디로 조회")
    public void testGetUser_Success() throws Exception {
        mockMvc.perform(get("/api/users/{id}", user1.getSeq()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("seq").value(user1.getSeq()))
                .andExpect(jsonPath("email").value(user1.getEmail()))
                .andExpect(jsonPath("login_count").value(user1.getLogin_count()))
                .andExpect(jsonPath("last_login_at").value(user1.getLast_login_at()))
                .andDo(print());
    }

    @Test
    @DisplayName("유저 아이디로 조회 실패 id가 없는경우")
    public void testGetUser_Fail_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 123))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("response.seq").exists())
                .andDo(print());
    }
}