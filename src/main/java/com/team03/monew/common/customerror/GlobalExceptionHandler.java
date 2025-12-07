package com.team03.monew.common.customerror;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //커스텀이나 설정한 에러 제외 모든 에러 잡는곳
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    //커스터 에러 잡는곳
    @ExceptionHandler(MonewException.class)
    public ResponseEntity<ErrorResponse> handleMonewException(MonewException ex) {
        log.error("커스텀 예외 발생: code={}, message={}", ex.getErrorCode(), ex.getMessage(), ex);
        HttpStatus httpStatus = ex.getErrorCode().getHttpStatus();
        ErrorResponse errorResponse = new ErrorResponse(ex,httpStatus.value());
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> MethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("요청 JOSN 값 유효성 검사 실패: {}",ex.getMessage());

        Map<String,Object> validationErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String filedName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(filedName,errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "VALIDATION_ERROR",
                "요청 JOSN 값 유효성 검사에 실패했습니다",
                validationErrors,
                ex.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
                );

        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> HandlerMethodValidationException(HandlerMethodValidationException ex) {
        log.error("요청 파라미터 값 유효성 검사 실패: {}",ex.getMessage());

        Map<String,Object> validationErrors = new HashMap<>();

        ex.getAllErrors().forEach((error) -> {
            String filedName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(filedName,errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "VALIDATION_ERROR",
                "요청 파라미터 값 유효성 검사에 실패했습니다",
                validationErrors,
                ex.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
        );

        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

}
