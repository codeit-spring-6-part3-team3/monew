package com.team03.monew.interest.service;

import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.fixture.SubscribeFixture;
import com.team03.monew.subscribe.repository.SubscribeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class InterestDeleteTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private SubscribeRepository subscribeRepository;

    @InjectMocks
    private BasicInterestService basicInterestService;

    @Test
    @DisplayName("관심사 삭제 성공 검증")
    public void interestDeleteTestSuccess() throws NoSuchObjectException {
        //given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Interest interest = InterestFixture.interestCreate("오늘의 뉴스", List.of("축구"));
        Subscribe subscribe = SubscribeFixture.subscribeCreate(userId, interestId);

        when(interestRepository.findById(any(UUID.class))).thenReturn(Optional.of(interest));
        when(subscribeRepository.findAll()).thenReturn(List.of(subscribe));
        //when
        basicInterestService.interestDelete(interestId);
        //then
        verify(interestRepository, times(1)).findById(any(UUID.class));
        verify(subscribeRepository, times(1)).findAll();
        verify(subscribeRepository, times(1)).deleteAll(anyList());
        verify(interestRepository, times(1)).delete(any(Interest.class));
    }

    @Test
    @DisplayName("관심사 삭제 관심사 정보 없음 실패 검증")
    public void interestDeleteInterestDateFail() {
        //when & then
        assertThatThrownBy(() -> basicInterestService.interestDelete(UUID.randomUUID()))
                .isInstanceOf(NoSuchObjectException.class)
                .hasMessage("관심사 정보 없음");
    }
}
