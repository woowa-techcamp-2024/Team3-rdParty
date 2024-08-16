package com.thirdparty.ticketing.domain.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C400", "올바르지 않은 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C401", "인증되지 않은 사용자 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C403", "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C404", "존재하지 않는 리소스입니다."),
    CONFLICT(HttpStatus.CONFLICT, "C409", "서버 리소스와 충돌이 발생했습니다."),
    VALIDATION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "C422", "유효성 검증에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C500", "서버 내부 에러입니다."),

    /*
       Member Error
    */
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "M404-1", "존재하지 않는 회원입니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "M409-1", "이메일이 중복되었습니다."),

    /*
       Access Token Error
    */
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "T401-1", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "T401-2", "토큰이 유효하지 않습니다."),
    INVALID_TOKEN_HEADER(HttpStatus.UNAUTHORIZED, "T401-2", "토큰 형식이 올바르지 않습니다."),

    /*
       Performance Error
    */
    NOT_FOUND_PERFORMANCE(HttpStatus.NOT_FOUND, "P404-1", "존재하지 않는 공연입니다."),

    /*
       Zone Error
    */
    NOT_FOUND_ZONE(HttpStatus.NOT_FOUND, "Z404-1", "존재하지 않는 구역입니다."),

    /*
       Seat Grade Error
    */
    NOT_FOUND_SEAT_GRADE(HttpStatus.NOT_FOUND, "SG404-1", "존재하지 않는 구역입니다."),

    /*
       Seat Error
    */
    NOT_FOUND_SEAT(HttpStatus.NOT_FOUND, "S404-1", "존재하지 않는 좌석입니다."),
    NOT_SELECTABLE_SEAT(HttpStatus.FORBIDDEN, "S403-1", "이미 선택된 좌석입니다."),
    INVALID_SEAT_STATUS(HttpStatus.FORBIDDEN, "S403-2", "요청을 수행할 수 없는 좌석의 상태입니다."),

    /*
       Payment Error
    */
    PAYMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "P500-1", "결제에 실패했습니다."),

    /*
       Waiting Error
    */
    WAITING_WRITE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "W500-1", "대기열 쓰기에 실패했습니다."),
    WAITING_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "W500-2", "대기열 읽기에 실패했습니다.");

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public int getHttpStatusValue() {
        return httpStatus.value();
    }
}
