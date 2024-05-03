package com.tobe.healthy.schedule.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@DynamicUpdate
public class ScheduleWaiting extends BaseTimeEntity<ScheduleWaiting, Long> {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "schedule_waiting_id")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "schedule_id")
	private Schedule schedule;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ColumnDefault("false")
	private boolean delYn = false;

	public static ScheduleWaiting register(Member member, Schedule schedule) {
		return ScheduleWaiting.builder()
			.schedule(schedule)
			.member(member)
			.build();
	}

	@Builder
	public ScheduleWaiting(Long id, Schedule schedule, Member member) {
		this.id = id;
		this.schedule = schedule;
		this.member = member;
	}
}
