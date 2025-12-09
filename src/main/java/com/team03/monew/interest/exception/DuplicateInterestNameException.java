package com.team03.monew.interest.exception;

import com.team03.monew.common.customerror.MonewException;

import static com.team03.monew.interest.exception.InterestErrorCode.DUPLICATE_INTEREST_NAME;

public class DuplicateInterestNameException extends MonewException {
    public DuplicateInterestNameException() {
        super(DUPLICATE_INTEREST_NAME);
    }
}
