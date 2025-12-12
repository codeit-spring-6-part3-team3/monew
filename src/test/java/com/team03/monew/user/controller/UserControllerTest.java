package com.team03.monew.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.user.exception.DuplicateEmailException;
import com.team03.monew.user.exception.InvalidPasswordException;
import com.team03.monew.user.exception.UserNotFoundException;
import com.team03.monew.user.dto.UserLoginRequest;
import com.team03.monew.user.dto.UserRegisterRequest;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("회원가입 API 성공")
    void register_success() throws Exception {
        // given
        UserRegisterRequest request = new UserRegisterRequest(
                "test@monew.com",
                "테스터",
                "test123!@#"
        );

        UUID testId = UUID.randomUUID();
        UserDto response = new UserDto(
                testId,
                request.getEmail(),
                request.getNickname(),
                LocalDateTime.now()
        );

        given(userService.register(any(UserRegisterRequest.class))).willReturn(response);

        // when ,then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.email").value("test@monew.com"))
                .andExpect(jsonPath("$.nickname").value("테스터"));
    }

    @Test
    @DisplayName("회원가입 API 실패 - 이메일 중복")
    void register_fail_duplicateEmail() throws Exception {
        // given
        UserRegisterRequest request = new UserRegisterRequest(
                "test@monew.com",
                "테스터",
                "test123!@#"
        );

        given(userService.register(any(UserRegisterRequest.class)))
                .willThrow(new DuplicateEmailException());

        // when ,then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));
    }

    @Test
    @DisplayName("회원가입 API 실패 - 유효성 검사 실패 (이메일 형식)")
    void register_fail_invalidEmail() throws Exception {
        // given
        UserRegisterRequest request = new UserRegisterRequest(
                "faild-email",  // 잘못된 이메일 형식
                "테스터",
                "test123!@#"
        );

        // when ,then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.email").exists());
    }

    @Test
    @DisplayName("회원가입 API 실패 - 유효성 검사 실패 (닉네임 길이)")
    void register_fail_invalidNickname() throws Exception {
        // given
        UserRegisterRequest request = new UserRegisterRequest(
                "test@monew.com",
                null,  // 2자 미만
                "test123!@#"
        );

        // when ,then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.nickname").exists());
    }

    @Test
    @DisplayName("로그인 API 성공")
    void login_success() throws Exception {
        // given
        UserLoginRequest request = new UserLoginRequest(
                "test@monew.com",
                "test123!@#"
        );

        UUID testId = UUID.randomUUID();
        UserDto response = new UserDto(
                testId,
                request.email(),
                "테스터",
                LocalDateTime.now()
        );

        given(userService.login(any(UserLoginRequest.class))).willReturn(response);

        // when, then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.email").value("test@monew.com"))
                .andExpect(jsonPath("$.nickname").value("테스터"));
    }

    @Test
    @DisplayName("로그인 API 실패 - 사용자 없음")
    void login_fail_userNotFound() throws Exception {
        // given
        UserLoginRequest request = new UserLoginRequest(
                "test@monew.com",
                "test123!@#"
        );

        given(userService.login(any(UserLoginRequest.class)))
                .willThrow(new UserNotFoundException());

        // when, then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("로그인 API 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() throws Exception {
        // given
        UserLoginRequest request = new UserLoginRequest(
                "test@monew.com",
                "wrong123!@#"
        );

        given(userService.login(any(UserLoginRequest.class)))
                .willThrow(new InvalidPasswordException());

        // when, then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_PASSWORD"))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("로그인 API 실패 - 유효성 검사 실패 (이메일 형식)")
    void login_fail_invalidEmail() throws Exception {
        // given
        UserLoginRequest request = new UserLoginRequest(
                "invalid-email",  // 잘못된 이메일 형식
                "test123!@#"
        );

        // when, then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.email").exists());
    }

}
