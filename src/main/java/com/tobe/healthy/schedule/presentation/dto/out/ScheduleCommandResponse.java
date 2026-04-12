package com.tobe.healthy.schedule.presentation.dto.out;

import java.util.List;

import com.tobe.healthy.member.domain.AlarmStatus;

public record ScheduleCommandResponse(
	AlarmStatus scheduleNoticeStatus,
	List<ScheduleCommandResult> morning,
	List<ScheduleCommandResult> afternoon
) {
	public static ScheduleCommandResponse create(AlarmStatus scheduleNoticeStatus, List<ScheduleCommandResult> morning,
		List<ScheduleCommandResult> afternoon) {
		return new ScheduleCommandResponse(
			scheduleNoticeStatus,
			morning.isEmpty() ? null : morning,
			afternoon.isEmpty() ? null : afternoon
		);
	}
}
