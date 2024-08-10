package com.thirdparty.ticketing.global.security;

import com.thirdparty.ticketing.domain.common.TicketingException;

public class AuthenticationException extends TicketingException {
    public AuthenticationException(String message) {
        super(message);
    }
}
