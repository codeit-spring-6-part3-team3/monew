package com.team03.monew.interest.mapper;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterestMapper {
    @Mapping(source = "subscribedByMe", target = "subscribedByMe")
    @Mapping(source = "interest.subscribeCount" , target = "subscriberCount")
    InterestDto toDto(Interest interest, Boolean subscribedByMe);
}
