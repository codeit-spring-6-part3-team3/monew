package com.team03.monew.subscribe.fixture;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.dto.SubscribeDto;

import java.util.UUID;

public class SubscribeFixture {

    public static Subscribe subscribeCreate(UUID userId, UUID interestId) {
        return Subscribe.builder()
                .userId(userId)
                .interestId(interestId)
                .build();
    }

    public static SubscribeDto subscribeCreateDto(Subscribe subscribe, Interest interest) {
        return SubscribeDto.builder()
                .id(subscribe.getId())
                .interestId(interest.getId())
                .interestName(interest.getName())
                .interestKeywords(interest.getKeywords())
                .interestSubscriberCount(interest.getSubscribeCount())
                .createdAt(interest.getCreatedAt())
                .build();
    }
}
