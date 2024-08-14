package com.thirdparty.ticketing.global.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(LoginMember.class);
        boolean hasLongParameterType = parameter.getParameterType().isAssignableFrom(String.class);
        return hasParameterAnnotation && hasLongParameterType;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        checkAuthenticated(authentication);
        return authentication.getPrincipal();
    }

    private void checkAuthenticated(Authentication jwtAuthentication) {
        if (jwtAuthentication != null) {
            return;
        }
        throw new TicketingException(ErrorCode.UNAUTHORIZED);
    }
}
