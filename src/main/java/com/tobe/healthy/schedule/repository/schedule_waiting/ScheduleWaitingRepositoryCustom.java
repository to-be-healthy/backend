package com.tobe.healthy.schedule.repository.schedule_waiting;

import com.tobe.healthy.schedule.domain.dto.out.MyScheduleWaiting;

import java.util.List;

public interface ScheduleWaitingRepositoryCustom {
    List<MyScheduleWaiting> findAllMyScheduleWaiting(Long memberId);
}
