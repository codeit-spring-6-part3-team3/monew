package com.team03.monew.interest.exception;

import com.team03.monew.common.customerror.MonewException;

import static com.team03.monew.interest.exception.InterestErrorCode.INTERESTS_NOT_FOUND;

public class InterestsNotFoundException extends MonewException {
    public InterestsNotFoundException() {
        
        super(INTERESTS_NOT_FOUND);
    }
}
