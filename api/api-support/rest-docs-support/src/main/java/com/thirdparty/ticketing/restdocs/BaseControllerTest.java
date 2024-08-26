package com.thirdparty.ticketing.restdocs;

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
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.jpa.member.Member;
import com.thirdparty.ticketing.jpa.member.MemberRole;
import com.thirdparty.ticketing.restdocs.BaseControllerTest.RestDocsConfig;
import com.thirdparty.ticketing.security.config.SecurityConfig;
import com.thirdparty.ticketing.security.config.WebConfig;
import com.thirdparty.ticketing.security.jwt.JwtProvider;

@Import({RestDocsConfig.class, SecurityConfig.class, WebConfig.class})
@ExtendWith(RestDocumentationExtension.class)
public abstract class BaseControllerTest {

    protected static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired protected JwtProvider jwtProvider;

    @Autowired protected ObjectMapper objectMapper;

    protected String adminBearerToken;

    protected String userBearerToken;

    protected MockMvc mockMvc;

    @Autowired protected RestDocumentationResultHandler restDocs;

    @TestConfiguration
    public static class RestDocsConfig {

        @Bean
        public RestDocumentationResultHandler write() {
            return MockMvcRestDocumentation.document(
                    "{class-name}/{method-name}",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));
        }
    }

    @BeforeEach
    void setUp(
            WebApplicationContext applicationContext,
            RestDocumentationContextProvider documentationContextProvider) {
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(applicationContext)
                        .alwaysDo(MockMvcResultHandlers.print())
                        .alwaysDo(restDocs)
                        .apply(SecurityMockMvcConfigurers.springSecurity())
                        .apply(
                                MockMvcRestDocumentation.documentationConfiguration(
                                        documentationContextProvider))
                        .addFilter(new CharacterEncodingFilter("UTF-8", true))
                        .build();

        Member admin = new Member("admin@admin.com", "password", MemberRole.ADMIN);
        this.adminBearerToken = "Bearer " + jwtProvider.createAccessToken(admin);

        Member user = new Member("user@user.com", "password", MemberRole.USER);
        this.userBearerToken = "Bearer " + jwtProvider.createAccessToken(user);
    }
}
