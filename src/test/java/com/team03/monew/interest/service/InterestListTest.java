package com.team03.monew.interest.service;

import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.CursorPageResponseInterestDto;
import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestSearchRequest;
import com.team03.monew.interest.mapper.InterestMapper;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.subscribe.repository.SubscribeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InterestListTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private SubscribeRepository subscribeRepository;

    @Mock
    private InterestMapper interestMapper;

    @InjectMocks
    private BasicInterestService basicInterestService;

    private UUID userId;
    private Interest interest1;
    private Interest interest2;
    private InterestDto interestDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        interest1 = InterestFixture.interestCreate("오늘의 뉴스", List.of("경제","주식"));
        ReflectionTestUtils.setField(interest1,"id",UUID.randomUUID());
        ReflectionTestUtils.setField(interest1,"createdAt",LocalDateTime.now());
        interest2 = InterestFixture.interestCreate("오늘의 뉴스과학", List.of("경제","주식"));
        ReflectionTestUtils.setField(interest2,"id",UUID.randomUUID());
        ReflectionTestUtils.setField(interest1,"createdAt",LocalDateTime.now());

        interestDto = InterestFixture.interestDtoCreate(interest1,true);

    }

    @Test
    @DisplayName("관심사 커서 없을때 목록 조회 성공 검증")
    public void interestListTestSuccess() {
        //given
        InterestSearchRequest req = InterestSearchRequest.builder()
                .keyword("오늘에 뉴스")
                .orderBy("name")
                .direction("ASC")
                .cursor(null)
                .after(null)
                .limit(1)
                .build();
        when(interestRepository.search(any(InterestSearchRequest.class))).thenReturn(List.of(interest1,interest2));
        when(interestRepository.totalElements(any(InterestSearchRequest.class))).thenReturn(2L);
        when(subscribeRepository.existsByUserIdAndInterestId(any(UUID.class), any(UUID.class))).thenReturn(true);
        when(interestMapper.toDto(any(Interest.class),any(Boolean.class))).thenReturn(interestDto);
        //when
        CursorPageResponseInterestDto response = basicInterestService.interestList(userId,req);
        //then
        //값 검증
        assertThat(response.content()).isEqualTo(List.of(interestDto));
        assertThat(response.nextCursor()).isEqualTo(interest1.getName());
        assertThat(response.nextAfter()).isEqualTo(interest1.getCreatedAt().toString());
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.totalElements()).isEqualTo(2L);
        assertThat(response.hasNext()).isTrue();

        //행위 검증
        verify(interestRepository,times(1)).search(any(InterestSearchRequest.class));
        verify(interestRepository,times(1)).totalElements(any(InterestSearchRequest.class));
        verify(subscribeRepository,times(1)).existsByUserIdAndInterestId(any(UUID.class), any(UUID.class));
        verify(interestMapper,times(1)).toDto(any(Interest.class),any(Boolean.class));
    }

    @Test
    @DisplayName("관심사 커서 있을때 목록 조회 성공 검증")
    public void interestListCursorTestSuccess() {
        //given
        InterestSearchRequest req = InterestSearchRequest.builder()
                .keyword("오늘에 뉴스")
                .orderBy("name")
                .direction("ASC")
                .cursor("오늘의 뉴스")
                .after(LocalDateTime.now().toString())
                .limit(1)
                .build();
        when(interestRepository.search(any(InterestSearchRequest.class))).thenReturn(List.of(interest2));
        when(interestRepository.totalElements(any(InterestSearchRequest.class))).thenReturn(1L);
        when(subscribeRepository.existsByUserIdAndInterestId(any(UUID.class), any(UUID.class))).thenReturn(true);
        when(interestMapper.toDto(any(Interest.class),any(Boolean.class))).thenReturn(interestDto);
        //when
        CursorPageResponseInterestDto response = basicInterestService.interestList(userId,req);
        //then
        //값 검증
        //값 검증
        assertThat(response.content()).isEqualTo(List.of(interestDto));
        assertThat(response.nextCursor()).isNull();
        assertThat(response.nextAfter()).isNull();
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.totalElements()).isEqualTo(1L);
        assertThat(response.hasNext()).isFalse();

        //행위 검증
        verify(interestRepository,times(1)).search(any(InterestSearchRequest.class));
        verify(interestRepository,times(1)).totalElements(any(InterestSearchRequest.class));
        verify(subscribeRepository,times(1)).existsByUserIdAndInterestId(any(UUID.class), any(UUID.class));
        verify(interestMapper,times(1)).toDto(any(Interest.class),any(Boolean.class));
    }
}
