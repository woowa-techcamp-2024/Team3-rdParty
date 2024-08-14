package com.thirdparty.ticketing.domain.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TicketingException.class)
    public ResponseEntity<ErrorResponse> handleTicketingException(TicketingException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatusValue())
                .body(ErrorResponse.of(errorCode));
    }
}
