package com.thirdparty.ticketing.domain.zone.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.zone.contoller.UserZoneController;
import com.thirdparty.ticketing.domain.zone.dto.ZoneElement;
import com.thirdparty.ticketing.domain.zone.service.UserZoneService;
import com.thirdparty.ticketing.support.BaseControllerTest;

@WebMvcTest(UserZoneController.class)
class UserZoneControllerTest extends BaseControllerTest {

    @MockBean private UserZoneService userZoneService;

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
                                pathParameters(
                                        parameterWithName("performanceId").description("공연 ID")),
                                responseFields(
                                        fieldWithPath("items").description("구역 목록"),
                                        fieldWithPath("items[].zoneId").description("구역 ID"),
                                        fieldWithPath("items[].zoneName").description("구역 이름"))));
    }
}
