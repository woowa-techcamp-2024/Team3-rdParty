package com.thirdparty.ticketing.domain.waitingsystem;

import com.thirdparty.ticketing.domain.common.EventPublisher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaitingSystem {

	private final WaitingManager waitingManager;
	private final RunningManager runningManager;
	private final EventPublisher eventPublisher;

	public boolean isReadyToHandle(String email, long performanceId) {
		return runningManager.isReadyToHandle(email, performanceId);
	}
}
