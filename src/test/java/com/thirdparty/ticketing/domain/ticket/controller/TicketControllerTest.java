package com.thirdparty.ticketing.domain.ticket.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.domain.seat.dto.response.SeatGradeElement;
import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.dto.response.TicketElement;
import com.thirdparty.ticketing.domain.ticket.dto.response.TicketSeatDetail;
import com.thirdparty.ticketing.domain.ticket.service.ReservationService;
import com.thirdparty.ticketing.domain.ticket.service.TicketService;
import com.thirdparty.ticketing.support.BaseControllerTest;

@WebMvcTest(controllers = TicketController.class)
class TicketControllerTest extends BaseControllerTest {

    public static final String PERFORMANCE_ID = "performanceId";
    @MockBean private TicketService ticketService;

    @MockBean private ReservationService reservationService;

    @Test
    @DisplayName("티켓 조회 API 호출 시")
    void selectMyTickets() throws Exception {
        // given
        PerformanceElement performanceElement =
                new PerformanceElement(1L, "흠뻑쇼", "서울", ZonedDateTime.now());
        SeatGradeElement seatGradeElement = new SeatGradeElement(1L, "VIP", 160000L);
        TicketSeatDetail seatElement = new TicketSeatDetail(2L, "A01", seatGradeElement);
        TicketElement ticketElement =
                new TicketElement(UUID.randomUUID(), performanceElement, seatElement);
        ItemResult<TicketElement> response = new ItemResult<>(List.of(ticketElement));

        given(ticketService.selectMyTicket(anyString())).willReturn(response);

        // when
        ResultActions result =
                mockMvc.perform(
                        get("/api/members/tickets").header(AUTHORIZATION_HEADER, userBearerToken));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("액세스 토큰")),
                                responseFields(
                                        fieldWithPath("items")
                                                .type(JsonFieldType.ARRAY)
                                                .description("티켓 목록"),
                                        fieldWithPath("items[].serialNumber")
                                                .type(JsonFieldType.STRING)
                                                .description("티켓 번호"),
                                        fieldWithPath("items[].seat")
                                                .type(JsonFieldType.OBJECT)
                                                .description("좌석 정보"),
                                        fieldWithPath("items[].seat.seatId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 ID"),
                                        fieldWithPath("items[].seat.seatCode")
                                                .type(JsonFieldType.STRING)
                                                .description("좌석 코드"),
                                        fieldWithPath("items[].seat.grade")
                                                .type(JsonFieldType.OBJECT)
                                                .description("좌석 등급"),
                                        fieldWithPath("items[].seat.grade.gradeId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 등급 ID"),
                                        fieldWithPath("items[].seat.grade.gradeName")
                                                .type(JsonFieldType.STRING)
                                                .description("좌석 등급 명"),
                                        fieldWithPath("items[].seat.grade.price")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 가격"),
                                        fieldWithPath("items[].performance.performanceId")
                                                .description("공연 ID")
                                                .type(JsonFieldType.NUMBER),
                                        fieldWithPath("items[].performance.performanceName")
                                                .description("공연 이름")
                                                .type(JsonFieldType.STRING),
                                        fieldWithPath("items[].performance.performancePlace")
                                                .description("공연 장소")
                                                .type(JsonFieldType.STRING),
                                        fieldWithPath("items[].performance.performanceShowtime")
                                                .description("공연 시간")
                                                .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("자리 선택 API 호출 시")
    void selectSeat() throws Exception {
        // given
        SeatSelectionRequest request = new SeatSelectionRequest();
        request.setSeatId(1L);

        // when
        ResultActions result =
                mockMvc.perform(
                        post("/api/seats/select")
                                .header(AUTHORIZATION_HEADER, userBearerToken)
                                .header(PERFORMANCE_ID, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request)));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("액세스 토큰"),
                                        headerWithName(PERFORMANCE_ID).description("공연 ID")),
                                requestFields(
                                        fieldWithPath("seatId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 ID"))));
    }

    @Test
    @DisplayName("티켓 결제 API 호출 시")
    void reservationTicket() throws Exception {
        // given
        TicketPaymentRequest request = new TicketPaymentRequest();
        request.setSeatId(1L);

        // when
        ResultActions result =
                mockMvc.perform(
                        post("/api/tickets")
                                .header(AUTHORIZATION_HEADER, userBearerToken)
                                .header(PERFORMANCE_ID, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request)));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("액세스 토큰"),
                                        headerWithName(PERFORMANCE_ID).description("공연 ID")),
                                requestFields(
                                        fieldWithPath("seatId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 ID"))));
    }

    @Test
    @DisplayName("좌석 점유 해제 API")
    void releaseSeat() throws Exception {
        // given
        SeatSelectionRequest request = new SeatSelectionRequest();
        request.setSeatId(1L);

        // when
        ResultActions result =
                mockMvc.perform(
                        post("/api/seats/release")
                                .header(AUTHORIZATION_HEADER, userBearerToken)
                                .header(PERFORMANCE_ID, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request)));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("액세스 토큰"),
                                        headerWithName(PERFORMANCE_ID).description("공연 ID")),
                                requestFields(
                                        fieldWithPath("seatId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("좌석 ID"))));
    }
}
