package com.thirdparty.ticketing.support;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.service.AuthService;
import com.thirdparty.ticketing.domain.member.service.JwtProvider;
import com.thirdparty.ticketing.domain.member.service.MemberService;
import com.thirdparty.ticketing.domain.performance.service.AdminPerformanceService;
import com.thirdparty.ticketing.domain.performance.service.UserPerformanceService;
import com.thirdparty.ticketing.domain.seat.service.AdminSeatService;
import com.thirdparty.ticketing.domain.seat.service.SeatService;
import com.thirdparty.ticketing.domain.ticket.service.ReservationService;
import com.thirdparty.ticketing.domain.ticket.service.TicketService;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingSystem;
import com.thirdparty.ticketing.domain.zone.service.AdminZoneService;
import com.thirdparty.ticketing.domain.zone.service.UserZoneService;
import com.thirdparty.ticketing.global.config.SecurityConfig;
import com.thirdparty.ticketing.global.config.WebConfig;
import com.thirdparty.ticketing.support.BaseControllerTest.RestDocsConfig;
import com.thirdparty.ticketing.support.controller.DocsController;
import com.thirdparty.ticketing.support.controller.ResolverTestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest
@Import({RestDocsConfig.class, SecurityConfig.class, WebConfig.class, DocsController.class, ResolverTestController.class})
@ExtendWith(RestDocumentationExtension.class)
public abstract class BaseControllerTest {

    protected static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired protected JwtProvider jwtProvider;

    @Autowired protected ObjectMapper objectMapper;

    @Autowired protected RestDocumentationResultHandler restDocs;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected TicketService ticketService;

    @MockBean
    protected AdminPerformanceService adminPerformanceService;

    @MockBean
    protected UserPerformanceService userPerformanceService;

    @MockBean
    protected AdminSeatService adminSeatService;

    @MockBean
    protected SeatService seatService;

    @MockBean
    protected AdminZoneService adminZoneService;

    @MockBean
    protected UserZoneService userZoneService;

    @MockBean
    protected ReservationService reservationService;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected WaitingSystem waitingSystem;

    protected String adminBearerToken;

    protected String userBearerToken;

    protected MockMvc mockMvc;

    @TestConfiguration
    public static class RestDocsConfig {

        @Bean
        public RestDocumentationResultHandler write() {
            return MockMvcRestDocumentation.document(
                    "{class-name}/{method-name}",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()));
        }
    }

    @BeforeEach
    void setUp(
            WebApplicationContext applicationContext,
            RestDocumentationContextProvider documentationContextProvider) {
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(applicationContext)
                        .alwaysDo(print())
                        .alwaysDo(restDocs)
                        .apply(springSecurity())
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
