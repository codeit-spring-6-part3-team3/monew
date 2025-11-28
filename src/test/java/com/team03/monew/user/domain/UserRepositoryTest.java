package com.team03.monew.user.domain;

import com.team03.monew.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 조회 성공")
    void saveAndFindUser() {
        // given
        User user = User.builder()
                .email("test@monew.com")
                .nickname("테스터")
                .password("test123!@#")
                .build();

        // when
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getUserId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@monew.com");
        assertThat(foundUser.get().getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void findByEmail() {
        // given
        User user = User.builder()
                .email("test@monew.com")
                .nickname("테스터")
                .password("test123!@#")
                .build();
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByEmail("test@monew.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void existsByEmail() {
        // given
        User user = User.builder()
                .email("test@monew.com")
                .nickname("테스터")
                .password("test123!@#")
                .build();
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByEmail("test@monew.com");
        boolean notExists = userRepository.existsByEmail("other@monew.com");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}