package com.team03.monew.subscribe.service;

import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.fixture.SubscribeFixture;
import com.team03.monew.subscribe.repository.SubscribeRepository;
import com.team03.monew.user.domain.User;
import com.team03.monew.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SubscribeDeleteTest {

    @Mock
    private SubscribeRepository subscribeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InterestRepository interestRepository;

    @InjectMocks
    private BasicSubscribeService basicSubscribeService;

    private User user;
    private Subscribe subscribe;
    private Interest interest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .nickname("nickname")
                .password("password")
                .build();
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        interest = InterestFixture.interestCreate("오늘에 뉴스", List.of("정치"));
        ReflectionTestUtils.setField(interest,"id",UUID.randomUUID());

        subscribe = SubscribeFixture.subscribeCreate(user.getId(),interest.getId());
        ReflectionTestUtils.setField(subscribe,"id",UUID.randomUUID());

    }


    @Test
    @DisplayName("구독 삭제 성공 검증")
    void subscribeDeleteSuccess() throws NoSuchObjectException {
        //given
        when(subscribeRepository.findByUserIdAndInterestId(any(UUID.class),any(UUID.class))).thenReturn(Optional.of(subscribe));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(interestRepository.findById(any(UUID.class))).thenReturn(Optional.of(interest));

        //when
        basicSubscribeService.subscribeDelete(subscribe.getUserId(),subscribe.getInterestId());
        //then
        verify(userRepository,times(1)).findById(any(UUID.class));
        verify(interestRepository,times(1)).findById(any(UUID.class));
        verify(subscribeRepository,times(1)).findByUserIdAndInterestId(any(UUID.class),any(UUID.class));
        verify(subscribeRepository,times(1)).delete(any(Subscribe.class));

    }

    @Test
    @DisplayName("구독 삭제 삭제 유저 정보 없음 실패 검증")
    void subscribeDeleteUserNotFoundFail() {
        //when & then
        assertThatThrownBy(() -> basicSubscribeService.subscribeDelete(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(NoSuchObjectException.class)
                .hasMessage("유저 정보 없음");
    }

    @Test
    @DisplayName("구독 삭제 삭제 관심사 정보 없음 실패 검증")
    void subscribeDeleteInterestNotFoundFail() {
        //given
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        //when & then
        assertThatThrownBy(() -> basicSubscribeService.subscribeDelete(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(NoSuchObjectException.class)
                .hasMessage("관심사 정보 없음");
    }

    @Test
    @DisplayName("구독 삭제 삭제 구독 정보 없음 실패 검증")
    void subscribeDeleteSubscribeNotFoundFail() {
        //given
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(interestRepository.findById(any(UUID.class))).thenReturn(Optional.of(interest));
        //when & then
        assertThatThrownBy(() -> basicSubscribeService.subscribeDelete(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(NoSuchObjectException.class)
                .hasMessage("구독 정보 없음");
    }


}
