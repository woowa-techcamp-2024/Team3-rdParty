package com.thirdparty.ticketing.domain.waitingroom;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class WaitingAspect {

    private final WaitingManager waitingManager;

    private Object waitingRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String)authentication.getPrincipal();
        UserInfo userInfo = new UserInfo(email);
        if(waitingManager.isReadyToHandle(userInfo)){
            return joinPoint.proceed();
        } else {
            long waitingNumber = waitingManager.enterWaitingRoom(userInfo);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).body(waitingNumber);
        }
    }
}
