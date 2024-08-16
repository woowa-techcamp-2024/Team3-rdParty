package com.thirdparty.ticketing.domain.waitingsystem;

public interface RunningManager {
	boolean isReadyToHandle(String email, long performanceId);
}
