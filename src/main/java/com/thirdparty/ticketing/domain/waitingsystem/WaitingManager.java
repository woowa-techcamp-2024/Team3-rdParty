package com.thirdparty.ticketing.domain.waitingsystem;

import java.util.Set;

import com.thirdparty.ticketing.domain.waiting.WaitingMember;

public interface WaitingManager {
	void enterWaitingRoom(String email, long performanceId);

	WaitingMember findWaitingMember(String email, long performanceId);

	Set<WaitingMember> pullOutMembers(long performanceId, long availableToRunning);
}
