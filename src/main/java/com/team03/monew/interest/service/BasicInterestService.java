package com.team03.monew.interest.service;

import com.sun.jdi.request.DuplicateRequestException;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.*;
import com.team03.monew.interest.mapper.InterestMapper;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicInterestService implements InterestService {

    private InterestRepository interestRepository;
    private SubscribeRepository subscribeRepository;
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

    public void interestDelete(UUID interest) throws NoSuchObjectException {
        Interest interestDelete = interestRepository.findById(interest)
                .orElseThrow(()-> new NoSuchObjectException("관심사 정보 없음"));

        List<Subscribe> subscribeList = subscribeRepository.findAll().stream()
                .filter(subscribe -> subscribe.getInterestId().equals(interestDelete.getId()))
                .toList();
        subscribeRepository.deleteAll(subscribeList);

        interestRepository.delete(interestDelete);
    }

    @Override
    public CursorPageResponseInterestDto interestList(UUID userId, InterestSearchRequest request) {

        //CursorPageResponseInterestDto 값
        List<Interest> interestList = interestRepository.search(request);
        String nextCursor = null;
        String nextAfter= null;
        Long totalElements = interestRepository.totalElements(request);
        boolean hasNext = interestList.size() == request.limit()+1;

        // hasNext가 true 면 마지막 확인 요소 제거
        // nextCursor, nextAfter 설정
        if (hasNext) {
            interestList = interestList.subList(0, request.limit());
            if(request.orderBy().equalsIgnoreCase("name")){
                nextCursor = interestList.get(interestList.size()-1).getName();
            }else {
                nextCursor = interestList.get(interestList.size()-1).getSubscribeCount().toString();
            }
            nextAfter = interestList.get(interestList.size()-1).getCreatedAt().toString();
        }


        // Interest -> InterestDto 변환
        List<InterestDto> interestDtoList = new ArrayList<>();
        for (Interest interest : interestList) {
            Boolean subscribedByMe = subscribeRepository.existsByUserIdAndInterestId(userId, interest.getId());
            interestDtoList.add(interestMapper.toDto(interest,subscribedByMe));
        }

        //반환형 조립
        return CursorPageResponseInterestDto.builder().
                content(interestDtoList)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(interestDtoList.size())
                .totalElements(totalElements)
                .hasNext(hasNext)
                .build();
    }

}
