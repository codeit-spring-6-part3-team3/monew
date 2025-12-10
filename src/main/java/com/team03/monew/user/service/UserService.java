package com.team03.monew.user.service;

import com.team03.monew.user.dto.UserLoginRequest;
import com.team03.monew.user.dto.UserRegisterRequest;
import com.team03.monew.user.dto.UserUpdateRequest;
import com.team03.monew.user.dto.UserDto;

import java.util.UUID;

public interface UserService {

    UserDto register(UserRegisterRequest request);

    UserDto login(UserLoginRequest request);

    UserDto findById(UUID userId);

    UserDto update(UUID userId, UserUpdateRequest request);

    void delete(UUID userId);

    void hardDelete(UUID userId);

    void hardDeleteExpiredUsers();
}
