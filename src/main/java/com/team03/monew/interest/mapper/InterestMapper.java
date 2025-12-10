package com.team03.monew.interest.mapper;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface InterestMapper {
    @Mapping(source = "subscribedByMe", target = "subscribedByMe")
    @Mapping(source = "interest.subscribeCount" , target = "subscriberCount")
    InterestDto toDto(Interest interest, Boolean subscribedByMe);

    default ArrayList<InterestDto> toDtoList(List<Interest> interestList, List<UUID> userInterestIds){

        ArrayList<InterestDto> dtoList = new ArrayList<>();

        for (Interest interest : interestList) {
            Boolean subscribedByMe = userInterestIds.contains(interest.getId());
            dtoList.add(toDto(interest,subscribedByMe));
        }
        return dtoList;
    };
}
