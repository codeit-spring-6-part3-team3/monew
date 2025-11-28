package com.team03.monew.interest.service;

import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestUpdateRequest;
import com.team03.monew.interest.mapper.InterestMapper;
import com.team03.monew.interest.repository.InterestRepository;
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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InterestUpdateTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private InterestMapper interestMapper;

    @InjectMocks
    private BasicInterestService basicInterestService;


    @Test
    @DisplayName("관심사 키워드 업데이트 성공 검증")
    void InterestUpdateSuccess() throws NoSuchObjectException {
        //given

        // request 값 준비
        List<String> list = List.of("축구","야구");
        InterestUpdateRequest request = new  InterestUpdateRequest(list);

        //DB에서 불러올 Interest 값 준비
        UUID interestId = UUID.randomUUID();
        Interest interest = InterestFixture.interestCreate("스포츠 뉴스",List.of("축구"));
        ReflectionTestUtils.setField(interest,"id",interestId);

        // Interest 키워드 변후 Interest 값 준비
        Interest newInterest = InterestFixture.interestCreate("스포츠 뉴스",list);
        ReflectionTestUtils.setField(newInterest,"id",interestId);
        InterestDto interestDto = InterestFixture.interestDtoCreate(newInterest);


        when(interestRepository.findById(any(UUID.class))).thenReturn(Optional.of(interest));
        when(interestMapper.toDto(any(Interest.class), isNull())).thenReturn(interestDto);

        //when
        InterestDto newInterestDto = basicInterestService.interestUpdate(interestId,request);

        //then
        //값 검증
        assertThat(newInterestDto.id()).isEqualTo(interestId);
        assertThat(newInterestDto.name()).isEqualTo(interest.getName());
        assertThat(newInterestDto.keywords()).isEqualTo(list);

        //
        verify(interestRepository, times(1)).findById(any(UUID.class));
        verify(interestRepository, times(1)).save(any(Interest.class));
        verify(interestMapper, times(1)).toDto(any(Interest.class), isNull());

    }

    @Test
    @DisplayName("관심사 업데이트 해당 관심사 없음 실패 검증")
    void InterestUpdateFailure(){

        UUID interestId = UUID.randomUUID();
        List<String> list = List.of("축구","야구");
        InterestUpdateRequest request = new  InterestUpdateRequest(list);

        assertThatThrownBy(() -> basicInterestService.interestUpdate(interestId,request))
                .isInstanceOf(NoSuchObjectException.class)
                .hasMessage("해당 관심사 없음");
    }

}
