package com.team03.monew.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL("U002", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD("U003", "비밀번호가 일치하지 않습니다.");

    private final String code;
    private final String message;
}

