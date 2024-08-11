package com.thirdparty.ticketing.domain.seat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.seat.controller.AdminSeatController;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationElement;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.service.AdminSeatService;
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

import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminSeatController.class)
@Import(AdminSeatController.class)
public class AdminSeatControllerTest extends BaseControllerTest {

    @MockBean
    private AdminSeatService adminSeatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/performances/{performancesId}/zones/{zoneId}/seats 요청")
    void createSeats() throws Exception {
        // given
        long performanceId = 1L;
        long zoneId = 2L;
        String content = createBodyContent();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/performances/{performanceId}/zones/{zoneId}/seats", performanceId, zoneId)
                .header(AUTHORIZATION_HEADER, adminBearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        // then
        result.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("performanceId").description("공연 ID"),
                                parameterWithName("zoneId").description("존 ID")
                        ),
                        requestFields(
                                fieldWithPath("seats[].seatCode").type(JsonFieldType.STRING).description("좌석 코드")
                        )
                ));
    }

    private String createBodyContent() throws JsonProcessingException {
        SeatCreationRequest seatCreationRequest = new SeatCreationRequest();
        SeatCreationElement seat1 = new SeatCreationElement();
        seat1.setSeatCode("A01");
        SeatCreationElement seat2 = new SeatCreationElement();
        seat2.setSeatCode("B01");

        seatCreationRequest.setSeats(List.of(
                seat1, seat2
        ));

        return objectMapper.writeValueAsString(seatCreationRequest);
    }
}

