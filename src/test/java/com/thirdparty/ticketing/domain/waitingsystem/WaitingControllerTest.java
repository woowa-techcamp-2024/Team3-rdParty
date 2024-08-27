package com.thirdparty.ticketing.domain.waitingsystem;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.thirdparty.ticketing.support.BaseControllerTest;

class WaitingControllerTest extends BaseControllerTest {

    public static final String PERFORMANCE_ID = "performanceId";

    @Test
    @DisplayName("남은 대기 순번 조회 API 호출 시")
    void getRemainingCount() throws Exception {
        // given
        given(waitingSystem.getRemainingCount(anyString(), anyLong())).willReturn(1L);

        // when
        ResultActions result =
                mockMvc.perform(
                        get("/api/performances/{performanceId}/wait", 1)
                                .header(AUTHORIZATION_HEADER, userBearerToken)
                                .header(PERFORMANCE_ID, 1));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("performanceId").description("공연 ID")),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("액세스 토큰"),
                                        headerWithName(PERFORMANCE_ID).description("공연 ID")),
                                responseFields(
                                        fieldWithPath("remainingCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("남은 대기 순번"))));
    }
}
