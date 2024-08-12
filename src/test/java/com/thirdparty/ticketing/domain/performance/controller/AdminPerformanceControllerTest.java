package com.thirdparty.ticketing.domain.performance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.performance.controller.request.PerformanceCreationRequest;
import com.thirdparty.ticketing.domain.performance.service.AdminPerformanceService;
import com.thirdparty.ticketing.support.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminPerformanceControllerTest.class)
@Import(AdminPerformanceController.class)
class AdminPerformanceControllerTest extends BaseControllerTest {

    @MockBean
    private AdminPerformanceService adminPerformanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/performances")
    void createPerformance() throws Exception {
        // given
        String content = createBodyContent();

        //when
        ResultActions result = mockMvc.perform(post("/api/performances")
                .header(AUTHORIZATION_HEADER, adminBearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        //then
        result.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("performanceName").type(JsonFieldType.STRING).description("공연 이름"),
                                fieldWithPath("performancePlace").type(JsonFieldType.STRING).description("공연 장소"),
                                fieldWithPath("performanceShowtime").type(JsonFieldType.STRING).description("공연 시간")
                        )
                ));
    }

    private String createBodyContent() throws JsonProcessingException {
        PerformanceCreationRequest pcr = new PerformanceCreationRequest();
        pcr.setPerformanceName("공연 이름");
        pcr.setPerformancePlace("공연 장소");
        pcr.setPerformanceShowtime(ZonedDateTime.now());
        return objectMapper.writeValueAsString(pcr);
    }

}
