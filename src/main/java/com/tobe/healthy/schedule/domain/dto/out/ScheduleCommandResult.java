package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.entity.ReserveType;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ScheduleCommandResult {
	private Long id;
	private LocalDateTime startDate;
	private ReserveType isReserve;
	private String round;
	private Member trainerId;
	private Member applicantId;

	public static ScheduleCommandResult of(Schedule schedule){
		return ScheduleCommandResult.builder()
			.id(schedule.getId())
			.startDate(schedule.getStartDate())
			.isReserve(schedule.getIsReserve())
			.round(schedule.getRound())
			.trainerId(schedule.getTrainerId())
			.applicantId(schedule.getApplicantId())
			.build();
	}
}
