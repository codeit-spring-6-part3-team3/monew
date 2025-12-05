package com.team03.monew.user.service;

import com.team03.monew.user.exception.DuplicateEmailException;
import com.team03.monew.user.domain.User;
import com.team03.monew.user.dto.UserRegisterRequest;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

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

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserDto response = userService.register(request);
        
        // then
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getNickname()).isEqualTo(request.getNickname());
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

}