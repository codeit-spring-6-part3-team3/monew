package com.team03.monew.subscribe.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.dto.SubscribeDto;
import com.team03.monew.subscribe.mapper.SubscribeMapper;
import com.team03.monew.subscribe.repository.SubscribeRepository;
import com.team03.monew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSubscribeService implements SubscribeService {

    private final UserRepository userRepository;
    private final SubscribeMapper subscribeMapper;
    private final InterestRepository interestRepository;
    private final SubscribeRepository subscribeRepository;


    //1 구독 기능 구독 생성
    @Override
    public SubscribeDto subscribeCreate(UUID userId, UUID interestId) throws NoSuchObjectException {

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchObjectException("유저 정보 없음"));

        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new NoSuchObjectException("관심사 정보 없음"));

        Subscribe subscribe = Subscribe.builder()
                .userId(userId)
                .interestId(interestId)
                .build();

        subscribeRepository.save(subscribe);

        return subscribeMapper.toDto(subscribe,interest);
    }

}
