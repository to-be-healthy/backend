package com.tobe.healthy.schedule.domain.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
public class StandBySchedule extends BaseTimeEntity<StandBySchedule, Long> {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "stand_by_schedule_id")
	private Long id;

	@OneToOne(fetch = LAZY, mappedBy = "standBySchedule")
	private Schedule schedule;

	@OneToOne(fetch = LAZY, cascade = ALL)
	@JoinColumn(name = "member_id")
	private Member member;

	public static StandBySchedule register(Member member, Schedule schedule) {
		StandBySchedule entity = StandBySchedule.builder()
			.schedule(schedule)
			.member(member)
			.build();
		return entity;
	}
}
