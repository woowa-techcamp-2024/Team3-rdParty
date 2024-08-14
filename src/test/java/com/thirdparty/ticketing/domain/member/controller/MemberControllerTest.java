package com.thirdparty.ticketing.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.member.dto.request.MemberCreationRequest;
import com.thirdparty.ticketing.domain.member.service.MemberService;
import com.thirdparty.ticketing.domain.member.service.response.CreateMemberResponse;
import com.thirdparty.ticketing.support.BaseControllerTest;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends BaseControllerTest {

    @Autowired private ObjectMapper objectMapper;

    @MockBean private MemberService memberService;

    @Test
    @DisplayName("회원 생성 API 호출")
    void createMember() throws Exception {
        // given
        MemberCreationRequest request = new MemberCreationRequest("eamil@email.com", "password");

        given(memberService.createMember(any())).willReturn(new CreateMemberResponse(1L));

        // when
        ResultActions result =
                mockMvc.perform(
                        post("/api/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request)));

        // then
        result.andExpect(status().isCreated())
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
                                                .description("회원ID"))));
    }
}
