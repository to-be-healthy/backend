package com.tobe.healthy.schedule.domain.entity;

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
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@DynamicUpdate
public class StandBySchedule extends BaseTimeEntity<StandBySchedule, Long> {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "stand_by_schedule_id")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "schedule_id")
	private Schedule schedule;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public static StandBySchedule register(Member member, Schedule schedule) {
		return StandBySchedule.builder()
			.schedule(schedule)
			.member(member)
			.build();
	}

	@Builder
	public StandBySchedule(Long id, Schedule schedule, Member member) {
		this.id = id;
		this.schedule = schedule;
		this.member = member;
	}

	public void registerSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
}
