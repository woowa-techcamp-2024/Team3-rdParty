package com.thirdparty.ticketing.domain.common;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TicketingException.class)
    public ResponseEntity<ErrorResponse<Void>> handleTicketingException(TicketingException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("예외 발생. 메세지={}", errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusValue())
                .body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<ValidationErrorDetail>>>
            handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse<List<ValidationErrorDetail>> errorResponse = ErrorResponse.of(e);
        log.info("API 요청 데이터 오류. 메세지={}", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
