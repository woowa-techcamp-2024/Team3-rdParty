package com.thirdparty.ticketing.domain.common;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String code;
    private final String message;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
}
