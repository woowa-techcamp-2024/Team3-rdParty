package com.thirdparty.ticketing.domain.waitingsystem;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
public class WaitingAspect {

    private final WaitingSystem waitingSystem;

    private Object waitingRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        Long performanceId = Long.valueOf(request.getHeader("performanceId"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        if (waitingSystem.isReadyToHandle(email, performanceId)) {
            return joinPoint.proceed();
        } else {
            waitingSystem.enterWaitingRoom(email, performanceId);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT);
        }
    }
}
