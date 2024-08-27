package com.thirdparty.ticketing.global.lock.lettuce;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LettuceLockAnnotation {
    String key(); // SpEL 표현식으로 Lock 키를 결정

    int retryLimit() default 5; // 기본 재시도 횟수

    int sleepDuration() default 300; // 기본 슬립 시간 (밀리초)
}
