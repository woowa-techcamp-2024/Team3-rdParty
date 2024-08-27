package com.thirdparty.ticketing.domain.zone.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.zone.dto.ZoneElement;
import com.thirdparty.ticketing.support.BaseControllerTest;

class UserZoneControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("GET /api/performances/{performanceId}/zones")
    void getZones() throws Exception {
        // given
        long performanceId = 1L;
        ZoneElement zoneElement = new ZoneElement(1L, "테스트 구역");

        given(userZoneService.getZones(performanceId))
                .willReturn(ItemResult.of(List.of(zoneElement)));

        // when
        ResultActions result =
                mockMvc.perform(
                        get("/api/performances/{performanceId}/zones", performanceId)
                                .header(AUTHORIZATION_HEADER, userBearerToken));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("액세스 토큰")),
                                pathParameters(
                                        parameterWithName("performanceId").description("공연 ID")),
                                responseFields(
                                        fieldWithPath("items").description("구역 목록"),
                                        fieldWithPath("items[].zoneId").description("구역 ID"),
                                        fieldWithPath("items[].zoneName").description("구역 이름"))));
    }
}
