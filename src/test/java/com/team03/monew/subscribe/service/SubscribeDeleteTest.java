package com.team03.monew.subscribe.service;

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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SubscribeDeleteTest {

    @Mock
    private SubscribeRepository subscribeRepository;

    @InjectMocks
    private BasicSubscribeService basicSubscribeService;

    @Test
    @DisplayName("구독 삭제 성공 검증")
    void subscribeDeleteSuccess() throws NoSuchObjectException {
        //given
        Subscribe subscribe = SubscribeFixture.subscribeCreate(UUID.randomUUID(), UUID.randomUUID());
        when(subscribeRepository.findByUserIdAndInterestId(any(UUID.class),any(UUID.class))).thenReturn(Optional.of(subscribe));

        //when
        basicSubscribeService.subscribeDelete(subscribe.getUserId(),subscribe.getInterestId());
        //then
        verify(subscribeRepository,times(1)).findByUserIdAndInterestId(any(UUID.class),any(UUID.class));
        verify(subscribeRepository,times(1)).delete(any(Subscribe.class));
    }

    @Test
    @DisplayName("구독 삭제 삭제 정보 없음 실패 검증")
    void subscribeDeleteFail() {
        //when & then
        assertThatThrownBy(() -> basicSubscribeService.subscribeDelete(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(NoSuchObjectException.class)
                .hasMessage("구독 정보 없음");
    }
}
