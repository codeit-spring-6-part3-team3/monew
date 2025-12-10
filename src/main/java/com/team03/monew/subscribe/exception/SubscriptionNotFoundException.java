package com.team03.monew.subscribe.exception;

import com.team03.monew.common.customerror.MonewException;

public class SubscriptionNotFoundException extends MonewException {
    public SubscriptionNotFoundException(SubscribeErrorCode errorCode) {
        super(errorCode);
    }
}
