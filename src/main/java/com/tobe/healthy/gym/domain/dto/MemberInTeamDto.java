package com.tobe.healthy.gym.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberInTeamDto {
	private Long memberId;
	private String name;
	private String userId;
	private String email;
	private int ranking;
	private int lessonCnt;
	private int remainLessonCnt;
	private LocalDate gymEndDt;

	@QueryProjection
	public MemberInTeamDto(Long memberId, String name, String userId, String email, int ranking, int lessonCnt, int remainLessonCnt, LocalDate gymEndDt) {
		this.memberId = memberId;
		this.name = name;
		this.userId = userId;
		this.email = email;
		this.ranking = ranking;
		this.lessonCnt = lessonCnt;
		this.remainLessonCnt = remainLessonCnt;
		this.gymEndDt = gymEndDt;
	}
}
