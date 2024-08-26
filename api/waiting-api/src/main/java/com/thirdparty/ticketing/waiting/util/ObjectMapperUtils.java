package com.thirdparty.ticketing.waiting.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;

public class ObjectMapperUtils {

    public static String writeValueAsString(ObjectMapper objectMapper, Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new TicketingException(ErrorCode.WAITING_WRITE_ERROR);
        }
    }

    public static <T> T readValue(ObjectMapper objectMapper, String value, Class<T> valueType) {
        try {
            return objectMapper.readValue(value, valueType);
        } catch (JsonProcessingException e) {
            throw new TicketingException(ErrorCode.WAITING_READ_ERROR);
        }
    }
}
