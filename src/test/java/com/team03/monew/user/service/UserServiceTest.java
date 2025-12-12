package com.team03.monew.user.service;

import com.team03.monew.user.exception.DuplicateEmailException;
import com.team03.monew.user.exception.InvalidPasswordException;
import com.team03.monew.user.exception.UserNotFoundException;
import com.team03.monew.user.domain.User;
import com.team03.monew.user.dto.UserLoginRequest;
import com.team03.monew.user.dto.UserRegisterRequest;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.mapper.UserMapper;
import com.team03.monew.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        // given
        UserRegisterRequest request = new UserRegisterRequest(
                "test@monew.com",
                "테스터",
                "test123!@#"
        );

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(request.getPassword())
                .build();

        UserDto expectedDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt()
        );

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

        // when
        UserDto response = userService.register(request);
        
        // then
        assertThat(response.email()).isEqualTo(request.getEmail());
        assertThat(response.nickname()).isEqualTo(request.getNickname());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_fail_duplicateEmail() {
        // given
        UserRegisterRequest request = new UserRegisterRequest(
                "test@monew.com",
                "테스터",
                "test123!@#"
        );

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        String email = "test@monew.com";
        String password = "test123!@#";

        UserLoginRequest request = new UserLoginRequest(email, password);

        User user = User.builder()
                .email(email)
                .nickname("테스터")
                .password(password)
                .build();

        UserDto expectedDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt()
        );

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto response = userService.login(request);

        // then
        assertThat(response.id()).isEqualTo(user.getId());
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.nickname()).isEqualTo("테스터");
        verify(userRepository).findByEmail(email);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void login_fail_userNotFound() {
        // given
        UserLoginRequest request = new UserLoginRequest("test@monew.com", "test123!@#");

        given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() {
        // given
        String email = "test@monew.com";
        String correctPassword = "test123!@#";
        String wrongPassword = "wrong123!@#";

        UserLoginRequest request = new UserLoginRequest(email, wrongPassword);

        User user = User.builder()
                .email(email)
                .nickname("테스터")
                .password(correctPassword)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }
}
