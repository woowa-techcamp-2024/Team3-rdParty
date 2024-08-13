package com.thirdparty.ticketing.domain.seat.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.seat.dto.response.SeatElement;
import com.thirdparty.ticketing.domain.seat.service.SeatService;
import com.thirdparty.ticketing.support.BaseControllerTest;

@WebMvcTest(SeatController.class)
public class SeatControllerTest extends BaseControllerTest {
    @MockBean private SeatService seatService;

    @Test
    @DisplayName("구역의 좌석 목록을 조회한다.")
    void getZones() throws Exception {
        // Given
        long performanceId = 1L;
        long zoneId = 2L;

        List<SeatElement> seatElements =
                Collections.singletonList(new SeatElement(1L, "A01", true));

        given(seatService.getSeats(any())).willReturn(ItemResult.of(seatElements));

        // When
        ResultActions result =
                mockMvc.perform(
                        get(
                                        "/api/performances/{performanceId}/zones/{zoneId}/seats",
                                        performanceId,
                                        zoneId)
                                .header(AUTHORIZATION_HEADER, adminBearerToken));

        // Then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("performanceId").description("공연 ID"),
                                        parameterWithName("zoneId").description("구역 ID")),
                                responseFields(
                                        fieldWithPath("items")
                                                .type(JsonFieldType.ARRAY)
                                                .description("좌석 목록"),
                                        fieldWithPath("items[].seatId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 ID"),
                                        fieldWithPath("items[].seatCode")
                                                .type(JsonFieldType.STRING)
                                                .description("좌석 코드"),
                                        fieldWithPath("items[].seatAvailable")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("좌석 선택 가능 여부"))));
    }
}
