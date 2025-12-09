package com.team03.monew.interest.exception;

import com.team03.monew.common.customerror.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InterestErrorCode implements ErrorCode {

    DUPLICATE_INTEREST_NAME(HttpStatus.REQUEST_TIMEOUT,"유사 관심사 중복"),
    INTERESTS_NOT_FOUND(HttpStatus.NOT_FOUND,"관심사 정보 없음");

    private final HttpStatus httpStatus;
    private final String message;
    InterestErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
