package com.tobe.healthy.schedule.presentation.dto.in;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandUpdateScheduleStatus {

	@NotNull(message = "수업 일정 ID를 입력해 주세요.")
	private List<Long> scheduleIds;
}
