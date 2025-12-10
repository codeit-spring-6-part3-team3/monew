package com.team03.monew.user.service;

import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.dto.UserLoginRequest;
import com.team03.monew.user.dto.UserRegisterRequest;
import com.team03.monew.user.dto.UserUpdateRequest;

import java.util.UUID;


public interface UserServiceInterface {
    
    // 사용자 회원가입

    UserDto register(UserRegisterRequest request);
    
    // 사용자 로그인

    UserDto login(UserLoginRequest request);
    UserDto findById(UUID userId);
    UserDto update(UUID userId, UserUpdateRequest request);

    void delete(UUID userId);

    void hardDelete(UUID userId);

    void hardDeleteExpiredUsers();
}




