package com.tobe.healthy.schedule.presentation.dto.out;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tobe.healthy.common.LessonDetailResultSerializer;
import com.tobe.healthy.common.LessonTimeFormatter;
import com.tobe.healthy.schedule.domain.entity.ReservationStatus;
import com.tobe.healthy.schedule.domain.entity.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveTrainerScheduleByLessonInfoResult {

	private static final double DEFAULT_DURATION = 60.0;
	private String trainerName;
	private String earliestLessonStartTime;
	private String latestLessonEndTime;
	private Map<LocalDate, List<LessonDetailResult>> schedule;

	public static RetrieveTrainerScheduleByLessonInfoResult from(List<Schedule> schedules) {
		LocalTime firstLessonStartTime = schedules.stream()
			.filter(s -> s.getReservationStatus() != ReservationStatus.DISABLED)
			.map(Schedule::getLessonStartTime)
			.min(LocalTime::compareTo)
			.orElse(null);

		LocalTime lastLessonEndTime = schedules.stream()
			.filter(s -> s.getReservationStatus() != ReservationStatus.DISABLED)
			.map(Schedule::getLessonEndTime)
			.max(LocalTime::compareTo)
			.orElse(null);

		Map<LocalDate, List<LessonDetailResult>> groupingSchedules = schedules.stream()
			.collect(Collectors.groupingBy(
				Schedule::getLessonDt,
				LinkedHashMap::new,
				Collectors.mapping(s -> new LessonDetailResult(
					s.getId(),
					calculateDuration(s),
					s.getLessonStartTime(),
					s.getLessonEndTime(),
					s.getReservationStatus(),
					s.getApplicant() != null ? s.getApplicant().getId() : null,
					s.getApplicant() != null ? s.getApplicant().getName() : null,
					s.getScheduleWaiting() != null && !s.getScheduleWaiting().isEmpty()
						? s.getScheduleWaiting().get(0).getMember().getId() : null,
					s.getScheduleWaiting() != null && !s.getScheduleWaiting().isEmpty()
						? s.getScheduleWaiting().get(0).getMember().getName() : null
				), Collectors.toList())
			));

		String trainerName = schedules.isEmpty() ? null
			: schedules.get(0).getTrainer() != null
			  ? schedules.get(0).getTrainer().getName() + " 트레이너"
			  : null;

		return RetrieveTrainerScheduleByLessonInfoResult.builder()
			.trainerName(trainerName)
			.schedule(groupingSchedules.isEmpty() ? null : groupingSchedules)
			.earliestLessonStartTime(LessonTimeFormatter.formatLessonTime(firstLessonStartTime))
			.latestLessonEndTime(LessonTimeFormatter.formatLessonTime(lastLessonEndTime))
			.build();
	}

	private static double calculateDuration(Schedule schedule) {
		double duration = Duration.between(schedule.getLessonStartTime(), schedule.getLessonEndTime())
			.toMinutes() / DEFAULT_DURATION;
		if (duration < 0) {
			return duration + 24;
		}
		return duration;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonSerialize(using = LessonDetailResultSerializer.class)
	public static class LessonDetailResult {

		private Long scheduleId;
		private Double duration;
		private LocalTime lessonStartTime;
		private LocalTime lessonEndTime;
		private ReservationStatus reservationStatus;
		private Long applicantId;
		private String applicantName;
		private Long waitingStudentId;
		private String waitingStudentName;
	}
}
