package com.tobe.healthy.schedule.presentation.dto.out;

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
public class RetrieveApplicantSchedule {

	private Long studentId;
	private String studentName;
	private Long scheduleId;
	private String lessonDt;
	private String lessonTime;
	private String attendanceStatus;

	public static RetrieveApplicantSchedule from(Schedule schedule) {
		return RetrieveApplicantSchedule.builder()
			.studentId(schedule.getApplicant() != null ? schedule.getApplicant().getId() : null)
			.studentName(schedule.getApplicant() != null ? schedule.getApplicant().getName() : null)
			.scheduleId(schedule.getId())
			.lessonDt(LessonTimeFormatter.formatLessonDt(schedule.getLessonDt()))
			.lessonTime(
				LessonTimeFormatter.formatLessonTime(schedule.getLessonStartTime(), schedule.getLessonEndTime()))
			.attendanceStatus(formatReservationStatus(schedule.getReservationStatus()))
			.build();
	}

	private static String formatReservationStatus(ReservationStatus reservationStatus) {
		if (reservationStatus == ReservationStatus.COMPLETED) {
			return "출석";
		} else if (reservationStatus == ReservationStatus.NO_SHOW) {
			return "미출석";
		} else {
			return reservationStatus.getDescription();
		}
	}
}
