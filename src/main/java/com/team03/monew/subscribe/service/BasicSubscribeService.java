package com.team03.monew.subscribe.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.exception.InterestsNotFoundException;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.dto.SubscribeDto;
import com.team03.monew.subscribe.mapper.SubscribeMapper;
import com.team03.monew.subscribe.repository.SubscribeRepository;
import com.team03.monew.user.exception.UserNotFoundException;
import com.team03.monew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.rmi.NoSuchObjectException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public SubscribeDto subscribeCreate(UUID userId, UUID interestId) {

        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(InterestsNotFoundException::new);

        Subscribe subscribe = Subscribe.builder()
                .userId(userId)
                .interestId(interestId)
                .build();
        interest.subscribeAdd();
        subscribeRepository.save(subscribe);
        interestRepository.save(interest);
        return subscribeMapper.toDto(subscribe,interest);
    }

    //2 구독 기능 구독 삭재
    @Override
    public void subscribeDelete(UUID userId, UUID interestId) {

        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(InterestsNotFoundException::new);

        Subscribe subscribe = subscribeRepository.findByUserIdAndInterestId(userId, interestId)
                .orElse(null);

        if (subscribe == null) {
            return;
        }

        interest.subscribeRemove();
        interestRepository.save(interest);
        subscribeRepository.delete(subscribe);
    }

    @Override
    public List<SubscribeDto> subscribeUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<Subscribe> subscribes = subscribeRepository.findByUserId(userId);
        if (subscribes.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> interestId = subscribes.stream()
                .map(Subscribe::getInterestId)
                .toList();

        Map<UUID,Interest> interests = interestRepository.findByIdIn(interestId).stream()
                .collect(Collectors.toMap(Interest::getId, Function.identity() ));

        return toDto(subscribes, interests);
    }

    public List<SubscribeDto> toDto(List<Subscribe> subscribes, Map<UUID,Interest> interests) {
        List<SubscribeDto> subscribeDtoList = new ArrayList<>();
        for (Subscribe subscribe : subscribes) {
            Interest interest = interests.get(subscribe.getInterestId());
            SubscribeDto subscribeDto = subscribeMapper.toDto(subscribe,interest);
            subscribeDtoList.add(subscribeDto);
        }
        return subscribeDtoList;
    }
}
