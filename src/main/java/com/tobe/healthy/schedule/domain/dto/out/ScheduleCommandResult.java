package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.entity.ReservationStatus;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import lombok.*;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.SOLD_OUT;

@Data
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleCommandResult {

	private Long scheduleId;
	private LocalDate lessonDt;
	private LocalTime lessonStartTime;
	private LocalTime lessonEndTime;
	private ReservationStatus reservationStatus;
	private String trainerName;
	private String applicantName;
	private String waitingByName;

	public static ScheduleCommandResult from(Schedule entity, Member member) {
		ScheduleCommandResultBuilder builder = ScheduleCommandResult.builder()
			.scheduleId(entity.getId())
			.lessonDt(entity.getLessonDt())
			.lessonStartTime(entity.getLessonStartTime())
			.lessonEndTime(entity.getLessonEndTime());

		if (!ObjectUtils.isEmpty(entity.getReservationStatus())) {
			builder.reservationStatus(entity.getReservationStatus());
		}

		if (!ObjectUtils.isEmpty(entity.getTrainer())) {
			builder.trainerName(entity.getTrainer().getName() + " 트레이너");
		}

		if (!ObjectUtils.isEmpty(entity.getApplicant())) {
			if (entity.getApplicant().getId().equals(member.getId()) && entity.getReservationStatus().equals(COMPLETED)) {
				builder.reservationStatus(SOLD_OUT);
			} else {
				builder.reservationStatus(entity.getReservationStatus());
			}
			builder.applicantName(entity.getApplicant().getName());
		}

		if (!ObjectUtils.isEmpty(entity.getScheduleWaiting())) {
			builder.waitingByName(entity.getScheduleWaiting().get(0).getMember().getName());
		}

		return builder.build();
	}

	public static ScheduleCommandResult from(Schedule entity) {
		ScheduleCommandResultBuilder builder = ScheduleCommandResult.builder()
				.scheduleId(entity.getId())
				.lessonDt(entity.getLessonDt())
				.lessonStartTime(entity.getLessonStartTime())
				.lessonEndTime(entity.getLessonEndTime())
				.reservationStatus(entity.getReservationStatus());

		if (!ObjectUtils.isEmpty(entity.getReservationStatus())) {
			builder.reservationStatus(entity.getReservationStatus());
		}

		if (!ObjectUtils.isEmpty(entity.getTrainer())) {
			builder.trainerName(entity.getTrainer().getName() + " 트레이너");
		}

		if (!ObjectUtils.isEmpty(entity.getApplicant())) {
			builder.applicantName(entity.getApplicant().getName());
		}

		if (!ObjectUtils.isEmpty(entity.getScheduleWaiting())) {
			builder.waitingByName(entity.getScheduleWaiting().get(0).getMember().getName());
		}

		return builder.build();
	}
}
