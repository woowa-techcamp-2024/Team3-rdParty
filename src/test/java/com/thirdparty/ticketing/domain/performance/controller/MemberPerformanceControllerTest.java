package com.thirdparty.ticketing.domain.performance.controller;

import com.thirdparty.ticketing.domain.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.domain.performance.service.MemberPerformanceService;
import com.thirdparty.ticketing.support.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberPerformanceControllerTest.class)
@Import(MemberPerformanceController.class)
class MemberPerformanceControllerTest extends BaseControllerTest {

    @MockBean
    private MemberPerformanceService memberPerformanceService;

    @Test
    @DisplayName("GET /api/performances")
    void getPerformances() throws Exception {
        //given
        PerformanceElement performanceElement = new PerformanceElement();
        performanceElement.setPerformanceId(1L);
        performanceElement.setPerformanceName("테스트 공연");
        performanceElement.setPerformancePlace("테스트 장소");
        performanceElement.setPerformanceShowtime(ZonedDateTime.now());

        when(memberPerformanceService.getPerformances()).thenReturn(List.of(performanceElement));

        //when
        ResultActions result = mockMvc.perform(get("/api/performances")
                .header(AUTHORIZATION_HEADER, userBearerToken));

        //then
        result.andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("item").description("공연 목록"),
                                fieldWithPath("item[].performanceId").description("공연 ID"),
                                fieldWithPath("item[].performanceName").description("공연 이름"),
                                fieldWithPath("item[].performancePlace").description("공연 장소"),
                                fieldWithPath("item[].performanceShowtime").description("공연 시간")
                        )
                ));
    }
}