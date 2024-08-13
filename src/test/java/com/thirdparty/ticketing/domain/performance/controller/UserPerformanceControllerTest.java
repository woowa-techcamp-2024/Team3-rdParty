package com.thirdparty.ticketing.domain.performance.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.ResultActions;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.domain.performance.service.UserPerformanceService;
import com.thirdparty.ticketing.support.BaseControllerTest;

@WebMvcTest(UserPerformanceControllerTest.class)
@Import(UserPerformanceController.class)
class UserPerformanceControllerTest extends BaseControllerTest {

    @MockBean private UserPerformanceService userPerformanceService;

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
                                        fieldWithPath("item").description("공연 목록"),
                                        fieldWithPath("item[].performanceId").description("공연 ID"),
                                        fieldWithPath("item[].performanceName")
                                                .description("공연 이름"),
                                        fieldWithPath("item[].performancePlace")
                                                .description("공연 장소"),
                                        fieldWithPath("item[].performanceShowtime")
                                                .description("공연 시간"))));
    }
}
