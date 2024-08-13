package com.thirdparty.ticketing.domain.seat.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thirdparty.ticketing.domain.seat.dto.request.SeatCreationElement;
import com.thirdparty.ticketing.domain.seat.dto.request.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.dto.request.SeatGradeCreationElement;
import com.thirdparty.ticketing.domain.seat.dto.request.SeatGradeCreationRequest;
import com.thirdparty.ticketing.domain.seat.service.AdminSeatService;
import com.thirdparty.ticketing.support.BaseControllerTest;

@WebMvcTest(AdminSeatController.class)
public class AdminSeatControllerTest extends BaseControllerTest {

    @MockBean private AdminSeatService adminSeatService;

    @Test
    @DisplayName("관리자 좌석 생성 API")
    void createSeats() throws Exception {
        // given
        long performanceId = 1L;
        long zoneId = 2L;
        String content = createBodyContent();

        // when
        ResultActions result =
                mockMvc.perform(
                        post(
                                        "/api/performances/{performanceId}/zones/{zoneId}/seats",
                                        performanceId,
                                        zoneId)
                                .header(AUTHORIZATION_HEADER, adminBearerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content));

        // then
        result.andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("performanceId").description("공연 ID"),
                                        parameterWithName("zoneId").description("존 ID")),
                                requestFields(
                                        fieldWithPath("seats[].seatCode")
                                                .type(JsonFieldType.STRING)
                                                .description("좌석 코드"),
                                        fieldWithPath("seats[].gradeId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 등급 id"))));
    }

    private String createBodyContent() throws JsonProcessingException {
        SeatCreationRequest seatCreationRequest = new SeatCreationRequest();
        SeatCreationElement seat1 = new SeatCreationElement();
        seat1.setSeatCode("A01");
        seat1.setGradeId(1L);
        SeatCreationElement seat2 = new SeatCreationElement();
        seat2.setSeatCode("B01");
        seat2.setGradeId(2L);

        seatCreationRequest.setSeats(List.of(seat1, seat2));

        return objectMapper.writeValueAsString(seatCreationRequest);
    }

    @Test
    @DisplayName("POST /api/performances/{performanceId}/grades 요청")
    void createSeatGrades() throws Exception {
        // given
        long performanceId = 1L;
        String content = makeRequest();

        // when
        ResultActions result =
                mockMvc.perform(
                        post("/api/performances/{performanceId}/grades", performanceId)
                                .header(AUTHORIZATION_HEADER, adminBearerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content));

        // then
        result.andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("performanceId").description("공연 ID")),
                                requestFields(
                                        fieldWithPath("seatGrades[].price")
                                                .type(JsonFieldType.NUMBER)
                                                .description("등급 가격"),
                                        fieldWithPath("seatGrades[].gradeName")
                                                .type(JsonFieldType.STRING)
                                                .description("좌석 등급명"))));
    }

    private String makeRequest() throws JsonProcessingException {
        SeatGradeCreationRequest request = new SeatGradeCreationRequest();

        SeatGradeCreationElement seatGrade1 = new SeatGradeCreationElement();
        seatGrade1.setGradeName("Grade1");
        seatGrade1.setPrice(10000L);
        SeatGradeCreationElement seatGrade2 = new SeatGradeCreationElement();
        seatGrade2.setGradeName("Grade2");
        seatGrade2.setPrice(20000L);

        request.setSeatGrades(List.of(seatGrade1, seatGrade2));

        return objectMapper.writeValueAsString(request);
    }
}
