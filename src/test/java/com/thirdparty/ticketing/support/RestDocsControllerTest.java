package com.thirdparty.ticketing.support;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.thirdparty.ticketing.global.config.SecurityConfig;
import com.thirdparty.ticketing.support.RestDocsControllerTest.RestDocsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@Import({RestDocsConfig.class, SecurityConfig.class})
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsControllerTest {

    @TestConfiguration
    public static class RestDocsConfig {

        @Bean
        public RestDocumentationResultHandler write() {
            return MockMvcRestDocumentation.document(
                    "{class-name}/{method-name}",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
            );
        }
    }

    protected MockMvc mockMvc;

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @BeforeEach
    void setUp(
            WebApplicationContext applicationContext,
            RestDocumentationContextProvider documentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .alwaysDo(print())
                .alwaysDo(restDocs)
                .apply(
                        MockMvcRestDocumentation.documentationConfiguration(documentationContextProvider))
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}
