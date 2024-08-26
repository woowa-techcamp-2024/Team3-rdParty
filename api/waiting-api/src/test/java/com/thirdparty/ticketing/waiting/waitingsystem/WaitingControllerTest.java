package com.thirdparty.ticketing.waiting.waitingsystem;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.ResultActions;

import com.thirdparty.ticketing.restdocs.BaseControllerTest;
import com.thirdparty.ticketing.waiting.WaitingController;

@WebMvcTest(controllers = WaitingController.class)
class WaitingControllerTest extends BaseControllerTest {

    public static final String PERFORMANCE_ID = "performanceId";

    @MockBean private WaitingSystem waitingSystem;

    @Test
    @DisplayName("남은 대기 순번 조회 API 호출 시")
    void getRemainingCount() throws Exception {
        // given
        given(waitingSystem.getRemainingCount(anyString(), anyLong())).willReturn(1L);

        // when
        ResultActions result =
                mockMvc.perform(
                        RestDocumentationRequestBuilders.get(
                                        "/api/performances/{performanceId}/wait", 1)
                                .header(AUTHORIZATION_HEADER, userBearerToken)
                                .header(PERFORMANCE_ID, 1));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                RequestDocumentation.pathParameters(
                                        RequestDocumentation.parameterWithName("performanceId")
                                                .description("공연 ID")),
                                HeaderDocumentation.requestHeaders(
                                        HeaderDocumentation.headerWithName(AUTHORIZATION_HEADER)
                                                .description("액세스 토큰"),
                                        HeaderDocumentation.headerWithName(PERFORMANCE_ID)
                                                .description("공연 ID")),
                                PayloadDocumentation.responseFields(
                                        PayloadDocumentation.fieldWithPath("remainingCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("남은 대기 순번"))));
    }
}
