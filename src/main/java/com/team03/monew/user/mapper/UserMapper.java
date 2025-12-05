package com.team03.monew.user.mapper;

import com.team03.monew.user.domain.User;
import com.team03.monew.user.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}

