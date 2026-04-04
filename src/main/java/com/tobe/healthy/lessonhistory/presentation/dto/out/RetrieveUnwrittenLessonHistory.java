package com.tobe.healthy.lessonhistory.presentation.dto.out;

import java.util.List;

import com.tobe.healthy.common.LessonTimeFormatter;
import com.tobe.healthy.lessonhistory.domain.LessonHistory;
import com.tobe.healthy.schedule.domain.ReservationStatus;
import com.tobe.healthy.schedule.domain.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveUnwrittenLessonHistory {

	private Long scheduleId;
	private Long studentId;
	private String studentName;
	private String lessonDt;
	private String lessonTime;
	private String reservationStatus;
	private Long lessonHistoryId;
	private String reviewStatus;

	public static RetrieveUnwrittenLessonHistory from(Schedule schedule) {
		return RetrieveUnwrittenLessonHistory.builder()
			.scheduleId(schedule.getId())
			.studentId(schedule.getApplicant() != null ? schedule.getApplicant().getId() : null)
			.studentName(schedule.getApplicant() != null ? schedule.getApplicant().getName() : null)
			.lessonDt(LessonTimeFormatter.formatLessonDt(schedule.getLessonDt()))
			.lessonTime(LessonTimeFormatter.formatLessonTimeWithAMPM(schedule.getLessonStartTime(),
				schedule.getLessonEndTime()))
			.reservationStatus(formatReservationStatus(schedule.getReservationStatus()))
			.lessonHistoryId(
				schedule.getLessonHistories().isEmpty() ? null : schedule.getLessonHistories().get(0).getId())
			.reviewStatus(validateReviewStatus(schedule.getLessonHistories()))
			.build();
	}

	private static String validateReviewStatus(List<LessonHistory> lessonHistories) {
		return lessonHistories.isEmpty() ? "미작성" : "작성";
	}

	private static String formatReservationStatus(ReservationStatus reservationStatus) {
		if (reservationStatus == ReservationStatus.COMPLETED) {
			return "출석";
		} else if (reservationStatus == ReservationStatus.NO_SHOW) {
			return "미출석";
		}
		return reservationStatus.getDescription();
	}
}
