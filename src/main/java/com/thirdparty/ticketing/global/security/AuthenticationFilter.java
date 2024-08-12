package com.thirdparty.ticketing.global.security;

import java.io.IOException;
import java.util.Objects;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.thirdparty.ticketing.domain.member.service.JwtProvider;
import com.thirdparty.ticketing.domain.member.service.response.CustomClaims;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("[auth] JWT 인증 인터셉터 시작");
        String bearerAccessToken = request.getHeader(HEADER);
        if (Objects.nonNull(bearerAccessToken)) {
            log.debug("[auth] JWT 인증 프로세스 시작");
            String accessToken = removeBearer(bearerAccessToken);
            JwtAuthentication jwtAuthentication = authenticate(accessToken);
            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            jwtAuthentication.getPrincipal(),
                            accessToken,
                            jwtAuthentication.getAuthorities().stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .toList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug(
                    "[auth] JWT 인증 프로세스 종료. 사용자 인증됨. principal={}",
                    jwtAuthentication.getPrincipal());
        }
        log.debug("[auth] Jwt 인증 인터셉터 종료");
        filterChain.doFilter(request, response);
    }

    private String removeBearer(String bearerAccessToken) {
        if (bearerAccessToken.contains(BEARER)) {
            return bearerAccessToken.replace(BEARER, "");
        }
        throw new AuthenticationException("액세스 토큰 형식이 옮바르지 않습니다.");
    }

    private JwtAuthentication authenticate(String accessToken) {
        CustomClaims customClaims = jwtProvider.parseAccessToken(accessToken);
        return new JwtAuthentication(
                customClaims.getEmail(), customClaims.getMemberRole(), accessToken);
    }
}
