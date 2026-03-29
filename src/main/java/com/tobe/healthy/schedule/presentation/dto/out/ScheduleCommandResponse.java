package com.tobe.healthy.schedule.presentation.dto.out;

import java.util.List;

import com.tobe.healthy.member.domain.entity.AlarmStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleCommandResponse {

	private AlarmStatus scheduleNoticeStatus;
	private List<ScheduleCommandResult> morning;
	private List<ScheduleCommandResult> afternoon;

	public static ScheduleCommandResponse create(AlarmStatus scheduleNoticeStatus, List<ScheduleCommandResult> morning,
		List<ScheduleCommandResult> afternoon) {
		return ScheduleCommandResponse.builder()
			.scheduleNoticeStatus(scheduleNoticeStatus)
			.morning(morning.isEmpty() ? null : morning)
			.afternoon(afternoon.isEmpty() ? null : afternoon)
			.build();
	}
}
