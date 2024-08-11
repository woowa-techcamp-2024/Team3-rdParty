package com.thirdparty.ticketing.global.security;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationContext {

    private final ThreadLocal<Authentication> context = new ThreadLocal<>();

    public void setAuthentication(Authentication authentication) {
        context.set(authentication);
        log.debug("[auth] 인증 컨텍스트 설정됨");
    }

    public Authentication getAuthentication() {
        return context.get();
    }

    public void releaseContext() {
        context.remove();
        log.debug("[auth] 인증 컨텍스트 소멸됨");
    }
}
