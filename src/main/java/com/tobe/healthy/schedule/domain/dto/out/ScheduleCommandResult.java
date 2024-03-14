package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.ReservationStatus;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleCommandResult {

	private Long scheduleId;
	private LocalDate lessonDt;
	private LocalTime lessonStartTime;
	private LocalTime lessonEndTime;
	private ReservationStatus reservationStatus;
	private int round;
	private String trainerName;
	private String applicantName;
	private String standByName;

	public static ScheduleCommandResult of(Schedule entity) {
		ScheduleCommandResultBuilder builder = ScheduleCommandResult.builder()
			.scheduleId(entity.getId())
			.lessonDt(entity.getLessonDt())
			.lessonStartTime(entity.getLessonStartTime())
			.lessonEndTime(entity.getLessonEndTime())
			.reservationStatus(entity.getReservationStatus())
			.round(entity.getRound());

		if (!ObjectUtils.isEmpty(entity.getTrainer())) {
			builder.trainerName(entity.getTrainer().getName());
		}

		if (!ObjectUtils.isEmpty(entity.getApplicant())) {
			builder.applicantName(entity.getApplicant().getName());
		}

		if (!ObjectUtils.isEmpty(entity.getStandBySchedule())) {
			builder.standByName(entity.getStandBySchedule().get(0).getMember().getName());
		}

		return builder.build();
	}
}
