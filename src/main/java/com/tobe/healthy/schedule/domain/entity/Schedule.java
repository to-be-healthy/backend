package com.tobe.healthy.schedule.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.*;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@DynamicUpdate
public class Schedule extends BaseTimeEntity<Schedule, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "schedule_id")
	private Long id;
	private LocalDate lessonDt;
	private LocalTime lessonStartTime;
	private LocalTime lessonEndTime;

	@Enumerated(STRING)
	private ReservationStatus reservationStatus = AVAILABLE;

	@ManyToOne(fetch = LAZY, cascade = PERSIST)
	@JoinColumn(name = "trainer_id")
	private Member trainer;

	@ManyToOne(fetch = LAZY, cascade = PERSIST)
	@JoinColumn(name = "applicant_id")
	@Nullable
	private Member applicant;

	@OneToMany(fetch = LAZY, mappedBy = "schedule", orphanRemoval = true)
	@Nullable
	private List<ScheduleWaiting> scheduleWaiting = new ArrayList<>();

	@ColumnDefault("false")
	private boolean delYn = false;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "course_id")
	private Course course;

	@OneToMany(fetch = LAZY, mappedBy = "schedule")
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
		this.reservationStatus = reservationStatus;
	}

	public void revertReservationStatusToNoShow() {
		this.reservationStatus = COMPLETED;
	}

	public void registerSchedule(Member member) {
		this.applicant = member;
		this.reservationStatus = COMPLETED;
	}

	public void cancelMemberSchedule() {
		this.reservationStatus = AVAILABLE;
		this.applicant = null;
	}

	public void cancelMemberSchedule(ScheduleWaiting scheduleWaiting) {
		this.reservationStatus = COMPLETED;
		this.applicant = scheduleWaiting.getMember();
	}

	public void changeApplicantInSchedule(Member applicant) {
		this.applicant = applicant;
	}

	@Builder
	public Schedule(Long id, LocalDate lessonDt, LocalTime lessonStartTime, LocalTime lessonEndTime,
					ReservationStatus reservationStatus, Member trainer, @Nullable Member applicant,
					@Nullable List<ScheduleWaiting> scheduleWaiting, boolean delYn) {
		this.id = id;
		this.lessonDt = lessonDt;
		this.lessonStartTime = lessonStartTime;
		this.lessonEndTime = lessonEndTime;
		this.reservationStatus = reservationStatus;
		this.trainer = trainer;
		this.applicant = applicant;
		this.scheduleWaiting = scheduleWaiting;
		this.delYn = delYn;
	}

	public void updateLessonDtToClosedDay() {
		this.reservationStatus = DISABLED;
		this.applicant = null;
	}

	public void registerCourse(Course course){
		this.course = course;
	}

	public void deleteCourse(){
		this.course = null;
	}
}
