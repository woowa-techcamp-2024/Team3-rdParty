package com.thirdparty.ticketing.domain.zone.controller;

import com.thirdparty.ticketing.domain.zone.contoller.MemberZoneController;
import com.thirdparty.ticketing.domain.zone.dto.ZoneElement;
import com.thirdparty.ticketing.domain.zone.service.MemberZoneService;
import com.thirdparty.ticketing.support.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberZoneControllerTest.class)
@Import(MemberZoneController.class)
class MemberZoneControllerTest extends BaseControllerTest {

    @MockBean
    private MemberZoneService memberZoneService;

    @Test
    @DisplayName("GET /api/performances/{performanceId}/zones")
    void getZones() throws Exception {
        // given
        long performanceId = 1L;
        ZoneElement zoneElement = new ZoneElement();
        zoneElement.setZoneId(1L);
        zoneElement.setZoneName("테스트 구역");

        when(memberZoneService.getZones(performanceId)).thenReturn(List.of(zoneElement));

        // when
        ResultActions result = mockMvc.perform(get("/api/performances/{performanceId}/zones", performanceId)
                .header(AUTHORIZATION_HEADER, userBearerToken));

        // then
        result.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("performanceId").description("공연 ID")
                        ),
                        responseFields(
                                fieldWithPath("item").description("구역 목록"),
                                fieldWithPath("item[].zoneId").description("구역 ID"),
                                fieldWithPath("item[].zoneName").description("구역 이름")
                        )
                ));
    }
}