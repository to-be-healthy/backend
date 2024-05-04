package com.tobe.healthy.schedule.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.*;
import static jakarta.persistence.CascadeType.ALL;
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

	@ManyToOne(fetch = LAZY, cascade = ALL)
	@JoinColumn(name = "trainer_id")
	private Member trainer;

	@ManyToOne(fetch = LAZY, cascade = ALL)
	@JoinColumn(name = "applicant_id")
	private Member applicant;

	@OneToMany(fetch = LAZY, mappedBy = "schedule")
	private List<ScheduleWaiting> scheduleWaiting = new ArrayList<>();

	@ColumnDefault("false")
	private boolean delYn = false;

	public static Schedule registerSchedule(LocalDate date, Member trainer, LocalTime startTime, LocalTime endTime, ReservationStatus reservationStatus) {
		ScheduleBuilder reserve = Schedule.builder()
				.lessonDt(date)
				.lessonStartTime(startTime)
				.lessonEndTime(endTime)
				.trainer(trainer)
				.reservationStatus(reservationStatus);

		return reserve.build();
	}

	public void updateReservationStatusToNoShow() {
		this.reservationStatus = NO_SHOW;
	}

	public void revertReservationStatusToNoShow() {
		this.reservationStatus = COMPLETED;
	}

	public void registerSchedule(Member member) {
		this.applicant = member;
		this.reservationStatus = COMPLETED;
	}

	public void cancelTrainerSchedule() {
		this.delYn = true;
		this.applicant = null;
	}

	public void cancelMemberSchedule() {
		this.reservationStatus = AVAILABLE;
		this.applicant = null;
	}

	public void changeApplicantInSchedule(Member applicant) {
		this.applicant = applicant;
	}

	@Builder
	public Schedule(Long id, LocalDate lessonDt, LocalTime lessonStartTime, LocalTime lessonEndTime,
					ReservationStatus reservationStatus, Member trainer, Member applicant,
					List<ScheduleWaiting> scheduleWaiting, boolean delYn) {
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
}
