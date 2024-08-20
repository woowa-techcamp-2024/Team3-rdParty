package com.thirdparty.ticketing.domain.waitingsystem;

import java.net.URI;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class WaitingAspect {

    private final WaitingSystem waitingSystem;

    @Around("@annotation(com.thirdparty.ticketing.domain.waitingsystem.Waiting)")
    private Object waitingRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        long performanceId =
                Optional.ofNullable(request.getHeader("performanceId"))
                        .map(Long::parseLong)
                        .orElseThrow(
                                () ->
                                        new TicketingException(
                                                ErrorCode.NOT_CONTAINS_PERFORMANCE_INFO));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        if (waitingSystem.isReadyToHandle(email, performanceId)) {
            return joinPoint.proceed();
        } else {
            waitingSystem.enterWaitingRoom(email, performanceId);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(URI.create("/api/performances/" + performanceId + "/wait"))
                    .build();
        }
    }
}
