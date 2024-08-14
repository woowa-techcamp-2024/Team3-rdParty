package com.thirdparty.ticketing.domain.common;

import java.util.List;

import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class ErrorResponse<T> {
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T details;

    public ErrorResponse(String code, String message, T details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public static <T> ErrorResponse<T> of(ErrorCode errorCode) {
        return new ErrorResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static ErrorResponse<List<ValidationErrorDetail>> of(
            MethodArgumentNotValidException exception) {
        List<ValidationErrorDetail> validationErrorDetails =
                ValidationErrorDetail.of(exception.getBindingResult());
        return ErrorResponse.of(ErrorCode.BAD_REQUEST, validationErrorDetails);
    }

    public static <T> ErrorResponse<T> of(ErrorCode errorCode, T details) {
        return new ErrorResponse<>(errorCode.getCode(), errorCode.getMessage(), details);
    }
}
