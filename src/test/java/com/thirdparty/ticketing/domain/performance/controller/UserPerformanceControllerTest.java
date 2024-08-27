package com.thirdparty.ticketing.domain.performance.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.support.BaseControllerTest;

class UserPerformanceControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("GET /api/performances")
    void getPerformances() throws Exception {
        // given
        PerformanceElement performanceElement =
                new PerformanceElement(1L, "테스트 공연", "테스트 장소", ZonedDateTime.now());

        given(userPerformanceService.getPerformances())
                .willReturn(ItemResult.of(List.of(performanceElement)));

        // when
        ResultActions result =
                mockMvc.perform(
                        get("/api/performances").header(AUTHORIZATION_HEADER, userBearerToken));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("items").description("공연 목록"),
                                        fieldWithPath("items[].performanceId").description("공연 ID"),
                                        fieldWithPath("items[].performanceName")
                                                .description("공연 이름"),
                                        fieldWithPath("items[].performancePlace")
                                                .description("공연 장소"),
                                        fieldWithPath("items[].performanceShowtime")
                                                .description("공연 시간"))));
    }

    @Test
    @DisplayName("공연 상세 정보 API")
    void getPerformance() throws Exception {
        // given
        Long performanceId = 1L;
        PerformanceElement performanceElement =
                new PerformanceElement(performanceId, "테스트 공연", "테스트 장소", ZonedDateTime.now());

        given(userPerformanceService.getPerformance(performanceId)).willReturn(performanceElement);

        // when
        ResultActions result =
                mockMvc.perform(
                        get("/api/performances/{performanceId}", performanceId)
                                .header(AUTHORIZATION_HEADER, userBearerToken));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("performanceId")
                                                .description("공연 ID")
                                                .type(JsonFieldType.NUMBER),
                                        fieldWithPath("performanceName")
                                                .description("공연 이름")
                                                .type(JsonFieldType.STRING),
                                        fieldWithPath("performancePlace")
                                                .description("공연 장소")
                                                .type(JsonFieldType.STRING),
                                        fieldWithPath("performanceShowtime")
                                                .description("공연 시간")
                                                .type(JsonFieldType.STRING))));
    }
}
