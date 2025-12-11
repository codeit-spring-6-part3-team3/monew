package com.team03.monew.subscribe.mapper;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.dto.SubscribeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface SubscribeMapper {


    @Mapping(source = "subscribe.id",target = "id")
    @Mapping(source = "interest.id",target = "interestId")
    @Mapping(source = "interest.name",target = "interestName")
    @Mapping(source = "interest.keywords", target = "interestKeywords")
    @Mapping(source = "interest.subscribeCount", target = "interestSubscriberCount")
    @Mapping(source = "subscribe.createdAt",target = "createdAt")
    SubscribeDto toDto(Subscribe subscribe , Interest interest);

}
