package com.tobe.healthy.member.domain.dto.out;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;


@Data
public class MemberInTeamResult {
	private Long memberId;
	private String name;
	private String userId;
	private String email;
	private int ranking;
	private int lessonCnt;
	private int remainLessonCnt;
	private String nickName;
	private String fileUrl;

	@QueryProjection
	public MemberInTeamResult(Long memberId, String name, String userId, String email, int ranking, int lessonCnt, int remainLessonCnt, String nickName, String fileUrl) {
		this.memberId = memberId;
		this.name = name;
		this.userId = userId;
		this.email = email;
		this.ranking = ranking;
		this.lessonCnt = lessonCnt;
		this.remainLessonCnt = remainLessonCnt;
		this.nickName = nickName;
		this.fileUrl = fileUrl;
	}
}
