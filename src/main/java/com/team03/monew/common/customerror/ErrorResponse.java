package com.team03.monew.common.customerror;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType,
        int status
) {
    public ErrorResponse(MonewException exception, int status) {
        this(
                LocalDateTime.now(),
                exception.getErrorCode().name(),
                exception.getMessage(),
                exception.getDetails(),
                exception.getClass().getSimpleName(),
                status
        );
    }

    public ErrorResponse(Exception exception, int status) {
        this(
                LocalDateTime.now(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                new HashMap<>(),
                exception.getClass().getSimpleName(),
                status
        );
    }
} 