package com.team03.monew.interest.Fixture;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestDto;

import java.util.List;

public class InterestFixture {

    public static Interest interestCreate(String name , List<String> keywords) {
        return Interest.builder()
                .name(name)
                .keywords(keywords)
                .build();
    }
    public static InterestDto interestDtoCreate(Interest interest,Boolean subscribedByMe) {
        return InterestDto.builder()
                .id(interest.getId())
                .name(interest.getName())
                .keywords(interest.getKeywords())
                .subscriberCount(interest.getSubscribeCount())
                .subscribedByMe(subscribedByMe)
                .build();
    }


}
