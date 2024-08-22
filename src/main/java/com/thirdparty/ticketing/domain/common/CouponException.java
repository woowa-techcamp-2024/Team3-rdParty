package com.thirdparty.ticketing.domain.common;

import lombok.Getter;

@Getter
public class CouponException extends RuntimeException {
    private final ErrorCode errorCode;

    public CouponException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CouponException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
}
