package com.tobe.healthy.schedule.repository.schedulewaiting;

import com.tobe.healthy.schedule.domain.dto.out.MyScheduleWaiting;

import java.util.List;

public interface ScheduleWaitingRepositoryCustom {
    List<MyScheduleWaiting> findAllMyScheduleWaiting(Long memberId);
}
