package com.team03.monew.subscribe.service;

import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.dto.SubscribeDto;
import com.team03.monew.subscribe.fixture.SubscribeFixture;
import com.team03.monew.subscribe.mapper.SubscribeMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscribeCreateTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscribeMapper subscribeMapper;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private SubscribeRepository subscribeRepository;

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
        ReflectionTestUtils.setField(user, "userId", UUID.randomUUID());

        interest = InterestFixture.interestCreate("오늘에 뉴스", List.of("정치"));
        ReflectionTestUtils.setField(interest,"id",UUID.randomUUID());

        subscribe = SubscribeFixture.subscribeCreate(user.getUserId(),interest.getId());
        ReflectionTestUtils.setField(subscribe,"id",UUID.randomUUID());

    }

    @Test
    @DisplayName("구독 추가 성공 검증")
    void SubscribeCreateSuccess() throws NoSuchObjectException {
        //given
        SubscribeDto subscribeDto = SubscribeFixture.subscribeCreateDto(subscribe,interest);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(interestRepository.findById(any(UUID.class))).thenReturn(Optional.of(interest));
        when(subscribeMapper.toDto(any(Subscribe.class), any(Interest.class))).thenReturn(subscribeDto);

        //when
        SubscribeDto newInterestDto = basicSubscribeService.subscribeCreate(user.getUserId(),interest.getId());

        //then
        //값 검증
        assertThat(newInterestDto.id()).isEqualTo(subscribe.getId());
        assertThat(newInterestDto.interestId()).isEqualTo(interest.getId());
        assertThat(newInterestDto.interestName()).isEqualTo(interest.getName());
        assertThat(newInterestDto.interestKeywords()).isEqualTo(interest.getKeywords());
        assertThat(newInterestDto.interestSubscriberCount()).isEqualTo(interest.getSubscribeCount());
        assertThat(newInterestDto.createdAt()).isEqualTo(interest.getCreatedAt());

        //행위 검증
        verify(userRepository,times(1)).findById(any(UUID.class));
        verify(interestRepository, times(1)).findById(any(UUID.class));
        verify(subscribeRepository,times(1)).save(any(Subscribe.class));
        verify(subscribeMapper,times(1)).toDto(any(Subscribe.class), any(Interest.class));
    }


    @Test
    @DisplayName("구독 추가 사용자 정보 없음 실패 검증")
    void SubscribeCreateUserDateFail() {
        //when & then
        assertThatThrownBy(() -> basicSubscribeService.subscribeCreate(user.getUserId(),interest.getId()))
                .isInstanceOf(NoSuchObjectException.class)
                .hasMessage("유저 정보 없음");
    }

    @Test
    @DisplayName("구독 추가 관심사 정보 없음 실패 검증")
    void SubscribeCreateInterestDateFail() {
        //given
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        //when & then
        assertThatThrownBy(() -> basicSubscribeService.subscribeCreate(user.getUserId(),interest.getId()))
                .isInstanceOf(NoSuchObjectException.class)
                .hasMessage("관심사 정보 없음");
    }
}
