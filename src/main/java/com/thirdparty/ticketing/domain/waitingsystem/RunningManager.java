package com.thirdparty.ticketing.domain.waitingsystem;

import java.util.Set;

public interface RunningManager {
	boolean isReadyToHandle(String email, long performanceId);

	long getRunningCount(long performanceId);

	long getAvailableToRunning(long performanceId);

	void enterRunningRoom(long performanceId, Set<WaitingMember> waitingMembers);
}
