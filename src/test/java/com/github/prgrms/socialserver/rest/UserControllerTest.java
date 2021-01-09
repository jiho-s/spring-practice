package com.github.prgrms.socialserver.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prgrms.socialserver.rest.dto.UserRequestDto;
import com.github.prgrms.socialserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @BeforeEach
    void setup() {
    }

    @Test
    @DisplayName("User 저장 성공")
    public void testCreateUser_Success() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto("test@email.com", "passwd");
        mockMvc.perform(post("/api/users/join")
                .content(objectMapper.writeValueAsString(userRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
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
                .andDo(print());
    }

    @Test
    @DisplayName("User 저장 실패 비밀번호가 공백인 경우")
    public void testCreateUser_Fail_BlankCredentials() throws Exception {
        mockMvc.perform(post("/api/users/join")
                .content("\"principal\":\"test@email.com\",\"credentials\":null")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}