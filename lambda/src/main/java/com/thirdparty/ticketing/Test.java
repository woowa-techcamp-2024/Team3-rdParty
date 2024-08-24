package com.thirdparty.ticketing;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.dto.SettingInfo;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String value = objectMapper.writeValueAsString(new SettingInfo("http://localhost:8080",
                0,
                1,
                5));
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(value);

        Handler handler = new Handler();
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, new MockContext());
        System.out.println(response.getBody());
    }
}
