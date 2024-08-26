package com.thirdparty.ticketing.jpa.common;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(updatable = false)
    private ZonedDateTime createdAt;

    @LastModifiedDate private ZonedDateTime updatedAt;

    public BaseEntity() {
        createdAt = ZonedDateTime.now();
    }

    public BaseEntity(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
