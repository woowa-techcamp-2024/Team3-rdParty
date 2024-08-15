package com.thirdparty.ticketing.domain.waiting;

import com.thirdparty.ticketing.domain.waiting.manager.WaitingManager;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Long performanceId = Long.valueOf(request.getHeader("performanceId"));
        WaitingMember waitingMember = new WaitingMember(email, performanceId);
        if (waitingManager.isReadyToHandle(waitingMember)) {
            return joinPoint.proceed();
        } else {
            long waitingNumber = waitingManager.enterWaitingRoom(waitingMember);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).body(waitingNumber);
        }
    }
}
