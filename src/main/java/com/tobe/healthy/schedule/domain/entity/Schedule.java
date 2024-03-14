package com.tobe.healthy.schedule.domain.entity;

import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.AVAILABLE;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest.ScheduleRegister;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.ObjectUtils;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
@ToString
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
	@Default
	private ReservationStatus reservationStatus = AVAILABLE;

	private int round;

	@ManyToOne(fetch = LAZY, cascade = ALL)
	@JoinColumn(name = "trainer_id")
	private Member trainer;

	@ManyToOne(fetch = LAZY, cascade = ALL)
	@JoinColumn(name = "applicant_id")
	private Member applicant;

	@OneToOne(mappedBy = "schedule")
	private StandBySchedule standBySchedule;

	@ColumnDefault("'N'")
	@Default
	private char delYn = 'N';

	public static Schedule registerSchedule(LocalDate date, Member trainer, Member member, ScheduleRegister request) {
		ScheduleBuilder reserve = Schedule.builder()
			.lessonDt(date)
			.round(request.getRound())
			.lessonStartTime(request.getStartTime())
			.lessonEndTime(request.getEndTime())
			.trainer(trainer)
			.reservationStatus(AVAILABLE);

		if (!ObjectUtils.isEmpty(member)) {
			reserve.applicant(member);
			reserve.reservationStatus(COMPLETED);
		}
		return reserve.build();
	}

	public void registerSchedule(Member member) {
		this.applicant = member;
		this.reservationStatus = COMPLETED;
	}

	public void cancelTrainerSchedule() {
		this.delYn = 'Y';
		this.applicant = null;
	}

	public void cancelMemberSchedule() {
		this.reservationStatus = AVAILABLE;
		this.applicant = null;
	}
}
