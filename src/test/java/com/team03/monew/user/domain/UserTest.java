package com.team03.monew.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("사용자 생성 성공")
    void createUser() {
        // given
        String email = "test@monew.com";
        String nickname = "테스터";
        String password = "test123!@#";

        // when
        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .build();

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("닉네임 수정 성공")
    void updateNickname() {
        // given
        User user = User.builder()
                .email("test@monew.com")
                .nickname("테스터")
                .password("test123!@#")
                .build();

        String newNickname = "새닉네임";

        // when
        user.updateNickname(newNickname);

        // then
        assertThat(user.getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("사용자 삭제 - 논리 삭제")
    void deleteUser() {
        // given
        User user = User.builder()
                .email("test@monew.com")
                .nickname("테스터")
                .password("test123!@#")
                .build();

        // when
        user.delete();

        // then
        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getDeletedAt()).isNotNull();
    }
}