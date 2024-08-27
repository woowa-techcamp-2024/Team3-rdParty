package com.thirdparty.ticketing.domain.common;

import lombok.Getter;

@Getter
public class TicketingException extends RuntimeException {
    private final ErrorCode errorCode;

    public TicketingException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public TicketingException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
}
