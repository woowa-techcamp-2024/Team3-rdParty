package com.thirdparty.ticketing.domain.member.service;

import com.thirdparty.ticketing.domain.common.TicketingException;

public class ExpiredTokenException extends TicketingException {
    public ExpiredTokenException(String message) {
        super(message);
    }
}
