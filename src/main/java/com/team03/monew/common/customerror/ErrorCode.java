package com.team03.monew.common.customerror;
import org.springframework.http.HttpStatus;
public interface ErrorCode {
    HttpStatus getHttpStatus();
    String getMessage();
    String name();
}
