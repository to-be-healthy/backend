package com.tobe.healthy.schedule.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.*;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@DynamicUpdate
@AllArgsConstructor
@Builder
@ToString
public class Schedule extends BaseTimeEntity<Schedule, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "schedule_id")
	private Long id;

	private LocalDate lessonDt;

	private LocalTime lessonStartTime;

	private LocalTime lessonEndTime;

	@Enumerated(STRING)
	@Builder.Default
	@ToString.Exclude
	private ReservationStatus reservationStatus = AVAILABLE;

	@ManyToOne(fetch = LAZY, cascade = PERSIST)
	@JoinColumn(name = "trainer_id")
	@ToString.Exclude
	private Member trainer;

	@ManyToOne(fetch = LAZY, cascade = PERSIST)
	@JoinColumn(name = "applicant_id")
	@ToString.Exclude
	@Nullable
	private Member applicant;

	@OneToMany(fetch = LAZY, mappedBy = "schedule", orphanRemoval = true, cascade = ALL)
	@Nullable
	@Builder.Default
	@ToString.Exclude
	private List<ScheduleWaiting> scheduleWaiting = new ArrayList<>();

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "course_id")
	@ToString.Exclude
	private Course course;

	@OneToMany(fetch = LAZY, mappedBy = "schedule")
	@Builder.Default
	@ToString.Exclude
	private List<LessonHistory> lessonHistories = new ArrayList<>();

    public static Schedule registerSchedule(LocalDate date, Member trainer, LocalTime startTime, LocalTime endTime, ReservationStatus reservationStatus) {
		ScheduleBuilder reserve = Schedule.builder()
				.lessonDt(date)
				.lessonStartTime(startTime)
				.lessonEndTime(endTime)
				.trainer(trainer)
				.reservationStatus(reservationStatus);

		return reserve.build();
	}

	public void updateReservationStatusToNoShow(ReservationStatus reservationStatus) {
		LocalDateTime lessonDateStartTime = LocalDateTime.of(lessonDt, lessonStartTime);
		if (LocalDateTime.now().isBefore(lessonDateStartTime)) {
			throw new IllegalArgumentException("수업 시작 이전에는 노쇼 처리를 할 수 없습니다.");
		}
		this.reservationStatus = reservationStatus;
	}

	public void registerSchedule(Member member) {
		this.applicant = member;
		this.reservationStatus = COMPLETED;
	}

	public void cancelMemberSchedule() {
		LocalDateTime lessonStartDateTime = LocalDateTime.of(lessonDt, lessonStartTime);
		if (LocalDateTime.now().isAfter(lessonStartDateTime)) {
			throw new IllegalArgumentException("수업 시작 이후에는 예약 취소가 불가능합니다.");
		}
		this.reservationStatus = AVAILABLE;
		this.applicant = null;
	}

	public void updateScheduleToDisabled() {
		this.reservationStatus = DISABLED;
		this.applicant = null;
	}

	public void registerCourse(Course course){
		this.course = course;
	}

	public void deleteCourse(){
		this.course = null;
	}

	public void updateLessonDtToAvailableDay() {
		this.reservationStatus = AVAILABLE;
		this.applicant = null;
	}
}
