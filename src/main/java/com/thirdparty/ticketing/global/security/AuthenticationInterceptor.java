package com.thirdparty.ticketing.global.security;

import com.thirdparty.ticketing.domain.member.service.JwtProvider;
import com.thirdparty.ticketing.domain.member.service.response.CustomClaims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;
    private final AuthenticationContext authenticationContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.debug("[auth] JWT 인증 인터셉터 시작");
        String bearerAccessToken = request.getHeader(HEADER);
        if(Objects.nonNull(bearerAccessToken)) {
            log.debug("[auth] JWT 인증 프로세스 시작");
            String accessToken = removeBearer(bearerAccessToken);
            Authentication authentication = authenticate(accessToken);
            authenticationContext.setAuthentication(authentication);
            log.debug("[auth] JWT 인증 프로세스 종료. 사용자 인증됨. principal={}", authentication.getPrincipal());
        }
        log.debug("[auth] Jwt 인증 인터셉터 종료");
        return true;
    }

    private String removeBearer(String bearerAccessToken) {
        if(bearerAccessToken.contains(BEARER)) {
            return bearerAccessToken.replace(BEARER, "");
        }
        throw new AuthenticationException("액세스 토큰 형식이 옮바르지 않습니다.");
    }

    private Authentication authenticate(String accessToken) {
        CustomClaims customClaims = jwtProvider.parseAccessToken(accessToken);
        return new Authentication(customClaims.getEmail(), customClaims.getMemberRole(), accessToken);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        authenticationContext.releaseContext();
    }
}
