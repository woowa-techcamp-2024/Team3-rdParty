package com.thirdparty.ticketing.support.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thirdparty.ticketing.support.BaseControllerTest;
import com.thirdparty.ticketing.support.controller.DocsController.HelloRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("API 문서 테스트 코드 작성 시")
public class DocumentationTest extends BaseControllerTest {

    @Test
    @DisplayName("GET 요청을 다음과 같이 문서화 할 수 있다.")
    public void getDocs() throws Exception {
        // given

        // when
        ResultActions result = mockMvc.perform(get("/test/docs/hello").param("name", "ticket"));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(parameterWithName("name").description("이름")),
                                responseFields(
                                        fieldWithPath("hello")
                                                .type(JsonFieldType.STRING)
                                                .description("안녕, 이름"))));
    }

    @Test
    @DisplayName("POST 요청은 다음과 같이 문서화 할 수 있다.")
    void postDocs() throws Exception {
        // given
        String content = objectMapper.writeValueAsString(new HelloRequest("name"));

        // when
        ResultActions result =
                mockMvc.perform(
                        post("/test/docs/hello/{test}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content));

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(parameterWithName("test").description("테스트용 경로 변수")),
                                requestFields(
                                        fieldWithPath("name")
                                                .type(JsonFieldType.STRING)
                                                .description("이름")),
                                responseFields(
                                        fieldWithPath("pathVariable")
                                                .type(JsonFieldType.STRING)
                                                .description("경로 변수"),
                                        fieldWithPath("hello")
                                                .type(JsonFieldType.STRING)
                                                .description("안녕, 이름"))));
    }
}
