package com.thirdparty.ticketing.domain.waitingsystem;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.thirdparty.ticketing.domain.waiting.manager.WaitingManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaitingAspect {

    private final WaitingManager waitingManager;

    private Object waitingRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        Long performanceId = Long.valueOf(request.getHeader("performanceId"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        WaitingMember waitingMember = new WaitingMember(email, performanceId);
        if (waitingManager.isReadyToHandle(waitingMember)) {
            return joinPoint.proceed();
        } else {
            long waitingNumber = waitingManager.enterWaitingRoom(waitingMember);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).body(waitingNumber);
        }
    }
}
