package com.thirdparty.ticketing.domain.member.service;

import com.thirdparty.ticketing.domain.common.TicketingException;

public class DuplicateResourceException extends TicketingException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
