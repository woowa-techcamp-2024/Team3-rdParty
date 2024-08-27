package com.thirdparty.ticketing.domain.member.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thirdparty.ticketing.domain.member.dto.request.LoginRequest;
import com.thirdparty.ticketing.domain.member.dto.response.LoginResponse;
import com.thirdparty.ticketing.support.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class AuthControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("로그인 API 호출 시")
    void login() throws Exception {
        // given
        LoginRequest request = new LoginRequest("email@email.com", "password");
        LoginResponse response = new LoginResponse(1L, "accessToken");

        given(authService.login(anyString(), anyString())).willReturn(response);

        // when
        ResultActions result =
                mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request)));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("email")
                                                .type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("password")
                                                .type(JsonFieldType.STRING)
                                                .description("비밀번호")),
                                responseFields(
                                        fieldWithPath("memberId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("회원 ID"),
                                        fieldWithPath("accessToken")
                                                .type(JsonFieldType.STRING)
                                                .description("액세스 토큰"))));
    }
}
