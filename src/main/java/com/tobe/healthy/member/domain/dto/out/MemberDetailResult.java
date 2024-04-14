package com.tobe.healthy.member.domain.dto.out;

import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.file.domain.dto.DietFileDto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberDetailResult {
	private Long memberId;
	private String name;
	private String nickName;
	private String fileUrl;
	private String memo;
	private int ranking;
	private int lessonCnt;
	private int remainLessonCnt;
	private LocalDate lessonDt;
	private LocalTime lessonStartTime;
	private DietDto diet;

	@QueryProjection
	public MemberDetailResult(Long memberId, String name, String nickName, String fileUrl, String memo, int ranking, int lessonCnt, int remainLessonCnt, LocalDate lessonDt, LocalTime lessonStartTime) {
		this.memberId = memberId;
		this.name = name;
		this.nickName = nickName;
		this.fileUrl = fileUrl;
		this.memo = memo;
		this.ranking = ranking;
		this.lessonCnt = lessonCnt;
		this.remainLessonCnt = remainLessonCnt;
		this.lessonDt = lessonDt;
		this.lessonStartTime = lessonStartTime;
	}

}
