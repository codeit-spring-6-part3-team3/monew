package com.team03.monew.user.controller;

import com.team03.monew.user.dto.UserLoginRequest;
import com.team03.monew.user.dto.UserRegisterRequest;
import com.team03.monew.user.dto.UserUpdateRequest;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.service.UserService;

import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 관리", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(
            operationId = "register",
            summary = "회원가입", 
            description = "새로운 사용자를 등록합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (이메일 중복, 유효성 검사 실패)"),
            @ApiResponse(responseCode = "409", description = "이메일 중복"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterRequest request) {
        UserDto response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            operationId = "login",
            summary = "로그인",
            description = "사용자 로그인을 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)"),
            @ApiResponse(responseCode = "401", description = "로그인 실패 (이메일 또는 비밀번호 불일치)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request) {
        UserDto response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            operationId = "update",
            summary = "사용자 정보 수정",
            description = "사용자 닉네임을 수정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검사 실패)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (사용자를 찾을 수 없음)"),
            @ApiResponse(responseCode = "409", description = "닉네임 중복"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        log.debug("사용자 업데이트 요청 수신: userId={}, nickname={}", userId, request.nickname());
        UserDto response = userService.update(userId, request);
        log.debug("사용자 업데이트 완료: userId={}", userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            operationId = "delete",
            summary = "사용자 논리 삭제",
            description = "사용자를 논리 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (사용자를 찾을 수 없음)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            operationId = "hardDelete",
            summary = "사용자 물리 삭제",
            description = "사용자를 물리 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (사용자를 찾을 수 없음)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> hardDelete(@PathVariable UUID userId) {
        userService.hardDelete(userId);
        return ResponseEntity.noContent().build();
    }

}
