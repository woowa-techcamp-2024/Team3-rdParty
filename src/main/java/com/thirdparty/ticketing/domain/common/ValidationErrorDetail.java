package com.thirdparty.ticketing.domain.common;

import java.util.List;

import org.springframework.validation.BindingResult;

import lombok.Data;

@Data
public class ValidationErrorDetail {
    private final String field;
    private final String value;
    private final String reason;

    public static List<ValidationErrorDetail> of(final BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(
                        error ->
                                new ValidationErrorDetail(
                                        error.getField(),
                                        error.getRejectedValue() != null
                                                ? error.getRejectedValue().toString()
                                                : null,
                                        error.getDefaultMessage()))
                .toList();
    }
}
