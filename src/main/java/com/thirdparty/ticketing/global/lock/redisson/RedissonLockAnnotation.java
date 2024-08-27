package com.thirdparty.ticketing.global.lock.redisson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLockAnnotation {
    String key(); // SpEL 표현식으로 Lock 키를 결정

    int waitTime() default 1; // 기본 대기 시간

    int lockTTL() default 60; // 락의 TTL
}
