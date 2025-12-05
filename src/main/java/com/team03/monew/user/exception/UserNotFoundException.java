package com.team03.monew.user.exception;




public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(){
        super(UserErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(String message){
        super(UserErrorCode.USER_NOT_FOUND, message);
    }
}
