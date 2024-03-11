package com.tobe.healthy.schedule.domain.entity;

import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.AVAILABLE;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest.ScheduleRegisterInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
@ToString(exclude = {"trainer", "applicant"})
public class Schedule extends BaseTimeEntity<Schedule, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "schedule_id")
	private Long id;

	private LocalDateTime startDt;

	private LocalDateTime endDt;

	@Enumerated(STRING)
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

	public static Schedule registerSchedule(Member trainer, Member member, ScheduleRegisterInfo request) {
		ScheduleBuilder reserve = Schedule.builder()
			.round(request.getRound())
			.startDt(request.getStartDt())
			.endDt(request.getEndDt())
			.trainer(trainer)
			.reservationStatus(AVAILABLE);

		if (!isEmpty(member)) {
			reserve.applicant(member);
			reserve.reservationStatus(COMPLETED);
		}

		return reserve.build();
	}

	public void cancelSchedule() {
		this.reservationStatus = AVAILABLE;
		this.applicant = null;
	}
}
