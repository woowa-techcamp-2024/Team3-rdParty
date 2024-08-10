package com.thirdparty.ticketing.support;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.support.DocumentationTest.DocsController;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = DocumentationTest.class)
@Import(DocsController.class)
@DisplayName("API 문서 테스트 코드 작성 시")
public class DocumentationTest extends RestDocsControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    public record HelloRequest(String name) {

    }

    @RestController
    @RequestMapping("/test/docs")
    public static class DocsController {

        @GetMapping("/hello")
        public ResponseEntity<Map<String, String>> hello(@RequestParam("name") String name) {
            Map<String, String> map = new HashMap<>();
            map.put("hello", name);
            return ResponseEntity.ok(map);
        }

        @PostMapping("/hello/{test}")
        public ResponseEntity<Map<String, String>> hello2(@PathVariable("test") Long testVariable,
                                                          @RequestBody HelloRequest request) {
            Map<String, String> map = new HashMap<>();
            map.put("hello", request.name);
            map.put("pathVariable", testVariable.toString());
            return ResponseEntity.ok(map);
        }
    }

    @Test
    @DisplayName("GET 요청을 다음과 같이 문서화 할 수 있다.")
    public void getDocs() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(get("/test/docs/hello")
                .param("name", "ticket"));

        //then
        result.andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("name").description("이름")
                        ),
                        responseFields(
                                fieldWithPath("hello").type(JsonFieldType.STRING).description("안녕, 이름")
                        )
                ));
    }

    @Test
    @DisplayName("POST 요청은 다음과 같이 문서화 할 수 있다.")
    void postDocs() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(new HelloRequest("name"));

        //when
        ResultActions result = mockMvc.perform(post("/test/docs/hello/{test}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        //then
        result.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("test").description("테스트용 경로 변수")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                        ),
                        responseFields(
                                fieldWithPath("pathVariable").type(JsonFieldType.STRING).description("경로 변수"),
                                fieldWithPath("hello").type(JsonFieldType.STRING).description("안녕, 이름")
                        )
                ));
    }
}
