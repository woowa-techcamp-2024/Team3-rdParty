package com.thirdparty.ticketing.domain.zone.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thirdparty.ticketing.domain.zone.dto.ZoneCreationElement;
import com.thirdparty.ticketing.domain.zone.dto.ZoneCreationRequest;
import com.thirdparty.ticketing.support.BaseControllerTest;

public class AdminZoneControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("POST /api/performances/{performanceId}/zones")
    void createZones() throws Exception {
        // given
        long performanceId = 1L;
        String content = createBodyContent();

        // when
        ResultActions result =
                mockMvc.perform(
                        post("/api/performances/{performanceId}/zones", performanceId)
                                .header(AUTHORIZATION_HEADER, adminBearerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content));

        // then
        result.andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER)
                                                .description("어드민 액세스 토큰")),
                                pathParameters(
                                        parameterWithName("performanceId").description("공연 ID")),
                                requestFields(
                                        fieldWithPath("zones[].zoneName")
                                                .type(JsonFieldType.STRING)
                                                .description("존 이름"))));
    }

    private String createBodyContent() throws JsonProcessingException {
        ZoneCreationRequest zoneCreationRequest = new ZoneCreationRequest();
        ZoneCreationElement zone1 = new ZoneCreationElement();
        zone1.setZoneName("VIP");
        ZoneCreationElement zone2 = new ZoneCreationElement();
        zone2.setZoneName("General");

        zoneCreationRequest.setZones(List.of(zone1, zone2));

        return objectMapper.writeValueAsString(zoneCreationRequest);
    }
}
