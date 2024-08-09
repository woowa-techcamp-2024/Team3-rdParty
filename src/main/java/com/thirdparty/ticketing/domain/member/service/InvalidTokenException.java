package com.thirdparty.ticketing.domain.member.service;

import com.thirdparty.ticketing.domain.common.TicketingException;

public class InvalidTokenException extends TicketingException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
