package com.team03.monew.interest.service;

import com.sun.jdi.request.DuplicateRequestException;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestRegisterRequest;
import com.team03.monew.interest.dto.InterestUpdateRequest;
import com.team03.monew.interest.mapper.InterestMapper;
import com.team03.monew.interest.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicInterestService implements InterestService {

    private InterestRepository interestRepository;
    private InterestMapper interestMapper;

    @Override
    public InterestDto interestCreate(InterestRegisterRequest request) {

        //관심사 이름 유사도 검사
        boolean NameDuplication = interestRepository.findAll().stream()
                .anyMatch( interest -> interest.nameEquals(request.name()));

        //관심사 이름 유사도 80% 이상 관심사 이름 중복으로 409코드 반환
        if(NameDuplication){
            // 커스텀 에러 추가 예정
            throw new DuplicateKeyException("이미 비슷한 관심사 이름있음");
        }

        //관심사 생성
        Interest interest = Interest.builder()
                .name(request.name())
                .keywords(request.keywords())
                .build();

        //관심사 객체 저장
        interestRepository.save(interest);

        return interestMapper.toDto(interest,null);
    }

    @Override
    public InterestDto interestUpdate(UUID interest,InterestUpdateRequest request) throws NoSuchObjectException {
        Interest interestUpdate = interestRepository.findById(interest)
                .orElseThrow(() -> new NoSuchObjectException("해당 관심사 없음"));

        interestUpdate.keywordUpdate(request.keywords());

        interestRepository.save(interestUpdate);
        return interestMapper.toDto(interestUpdate,null);
    }

}
