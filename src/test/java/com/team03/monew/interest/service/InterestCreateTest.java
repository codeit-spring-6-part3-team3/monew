package com.team03.monew.interest.service;


import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestRegisterRequest;
import com.team03.monew.interest.mapper.InterestMapper;
import com.team03.monew.interest.repository.InterestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class InterestCreateTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private InterestMapper interestMapper;

    @InjectMocks
    private BasicInterestService basicInterestService;

    @Test
    @DisplayName("관심사_등록_성공_검증")
    void interestCreateSuccess() {
        //given
        InterestRegisterRequest request = new InterestRegisterRequest("오늘에 과학", List.of("누리호", "천문학"));
        Interest interest = InterestFixture.interestCreate("오늘에 과학", List.of("누리호", "천문학"));
        InterestDto interestDto = InterestFixture.interestDtoCreate(interest,null);
        when(interestMapper.toDto(any(Interest.class), isNull())).thenReturn(interestDto);

        //when
        InterestDto newInterestDto = basicInterestService.interestCreate(request);

        //then
        //값검증
        assertThat(newInterestDto.name()).isEqualTo(request.name());
        assertThat(newInterestDto.keywords()).isEqualTo(request.keywords());

        //행위 검증
        verify(interestRepository, times(1)).save(any(Interest.class));
        verify(interestMapper, times(1)).toDto(any(Interest.class), isNull());
    }

    @Test
    @DisplayName("관심사_이름_중복_실패_검증")
    void interestCreateDuplicateNameFail() {
        //given
        InterestRegisterRequest request = new InterestRegisterRequest("오늘에 과학뉴스", List.of("누리호", "천문학"));
        Interest interest1 = InterestFixture.interestCreate("오늘에 과학", List.of("누리호", "천문학"));

        when(interestRepository.findAll()).thenReturn(List.of(interest1));

        //when&then
        assertThatThrownBy(() -> basicInterestService.interestCreate(request))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("이미 비슷한 관심사 이름있음");
    }

}
