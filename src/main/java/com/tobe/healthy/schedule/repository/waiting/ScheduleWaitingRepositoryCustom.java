package com.tobe.healthy.schedule.repository.waiting;

import java.util.List;

import com.tobe.healthy.schedule.domain.dto.out.MyScheduleWaiting;

public interface ScheduleWaitingRepositoryCustom {
	List<MyScheduleWaiting> findAllMyScheduleWaiting(Long memberId);
}
