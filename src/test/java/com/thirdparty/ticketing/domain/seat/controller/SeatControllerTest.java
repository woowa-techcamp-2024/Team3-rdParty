package com.thirdparty.ticketing.domain.seat.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.seat.dto.response.SeatElement;
import com.thirdparty.ticketing.domain.seat.dto.response.SeatGradeElement;
import com.thirdparty.ticketing.domain.seat.service.SeatService;
import com.thirdparty.ticketing.support.BaseControllerTest;

@WebMvcTest(SeatController.class)
public class SeatControllerTest extends BaseControllerTest {
    @MockBean private SeatService seatService;

    @Test
    @DisplayName("구역의 좌석 목록을 조회한다.")
    void getSeats() throws Exception {
        // Given
        long performanceId = 1L;
        long zoneId = 2L;

        List<SeatElement> seatElements =
                List.of(new SeatElement(1L, "A01", true), new SeatElement(2L, "B01", false));

        given(seatService.getSeats(anyLong())).willReturn(ItemResult.of(seatElements));

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

    @Test
    @DisplayName("공연의 좌석 등급 목록을 조회한다.")
    void getSeatGrades() throws Exception {
        // Given
        long performanceId = 1L;

        List<SeatGradeElement> seatGradeElements =
                List.of(
                        new SeatGradeElement(1L, "Grade1", 10000L),
                        new SeatGradeElement(2L, "Grade2", 20000L));

        given(seatService.getSeatGrades(anyLong())).willReturn(ItemResult.of(seatGradeElements));

        // When
        ResultActions result =
                mockMvc.perform(
                        get("/api/performances/{performanceId}/grades", performanceId)
                                .header(AUTHORIZATION_HEADER, adminBearerToken));

        // Then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("performanceId").description("공연 ID")),
                                responseFields(
                                        fieldWithPath("items")
                                                .type(JsonFieldType.ARRAY)
                                                .description("좌석 등급 목록"),
                                        fieldWithPath("items[].gradeId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 등급 ID"),
                                        fieldWithPath("items[].gradeName")
                                                .type(JsonFieldType.STRING)
                                                .description("좌석 등급 이름"),
                                        fieldWithPath("items[].price")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 등급 가격"))));
    }
}
