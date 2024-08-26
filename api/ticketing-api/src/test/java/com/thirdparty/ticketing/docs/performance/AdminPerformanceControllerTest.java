package com.thirdparty.ticketing.docs.performance;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thirdparty.ticketing.performance.controller.AdminPerformanceController;
import com.thirdparty.ticketing.performance.dto.request.PerformanceCreationRequest;
import com.thirdparty.ticketing.performance.service.AdminPerformanceService;
import com.thirdparty.ticketing.restdocs.BaseControllerTest;

@WebMvcTest(AdminPerformanceController.class)
class AdminPerformanceControllerTest extends BaseControllerTest {

    @MockBean private AdminPerformanceService adminPerformanceService;

    @Test
    @DisplayName("POST /api/performances")
    void createPerformance() throws Exception {
        // given
        String content = createBodyContent();

        // when
        ResultActions result =
                mockMvc.perform(
                        post("/api/performances")
                                .header(AUTHORIZATION_HEADER, adminBearerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content));

        // then
        result.andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("액세스 토큰")),
                                requestFields(
                                        fieldWithPath("performanceName")
                                                .type(JsonFieldType.STRING)
                                                .description("공연 이름"),
                                        fieldWithPath("performancePlace")
                                                .type(JsonFieldType.STRING)
                                                .description("공연 장소"),
                                        fieldWithPath("performanceShowtime")
                                                .type(JsonFieldType.STRING)
                                                .description("공연 시간"))));
    }

    private String createBodyContent() throws JsonProcessingException {
        PerformanceCreationRequest pcr = new PerformanceCreationRequest();
        pcr.setPerformanceName("공연 이름");
        pcr.setPerformancePlace("공연 장소");
        pcr.setPerformanceShowtime(ZonedDateTime.now());
        return objectMapper.writeValueAsString(pcr);
    }
}
