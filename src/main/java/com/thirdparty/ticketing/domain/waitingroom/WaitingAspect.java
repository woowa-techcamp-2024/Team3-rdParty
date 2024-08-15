package com.thirdparty.ticketing.domain.waitingroom;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
public class WaitingAspect {

    private final WaitingManager waitingManager;

    private Object waitingRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String performanceId = request.getParameter("performanceId");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        UserInfo userInfo = new UserInfo(email, performanceId);
        if (waitingManager.isReadyToHandle(userInfo)) {
            return joinPoint.proceed();
        } else {
            long waitingNumber = waitingManager.enterWaitingRoom(userInfo);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).body(waitingNumber);
        }
    }
}
