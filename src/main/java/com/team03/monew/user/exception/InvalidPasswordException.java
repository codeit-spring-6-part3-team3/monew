package com.team03.monew.user.exception;

public class InvalidPasswordException extends BusinessException {

    public InvalidPasswordException() {
        super(UserErrorCode.INVALID_PASSWORD);
    }

    public InvalidPasswordException(String message) {
        super(UserErrorCode.INVALID_PASSWORD, message);
    }
}




