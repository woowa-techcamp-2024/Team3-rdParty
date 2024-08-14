package com.thirdparty.ticketing.domain.common;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TicketingException.class)
    public ResponseEntity<ErrorResponse<Void>> handleTicketingException(TicketingException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatusValue())
                .body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<ValidationErrorDetail>>>
            handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse<List<ValidationErrorDetail>> errorResponse = ErrorResponse.of(e);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
