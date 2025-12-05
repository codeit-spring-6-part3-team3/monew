package com.team03.monew.user.exception;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException(){
        super(UserErrorCode.DUPLICATE_EMAIL);
    }

    public DuplicateEmailException(String message){
        super(UserErrorCode.DUPLICATE_EMAIL, message);
    }
}
