package com.tobe.healthy.member.domain.dto.out;

import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.diet.domain.dto.DietDto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MemberDetailResult {
	private Long memberId;
	private String name;
	private String nickName;
	private String fileUrl;
	private String memo;
	private int ranking;
	private LocalDate lessonDt;
	private LocalTime lessonStartTime;
	private DietDto diet;
	private CourseDto course;

	@QueryProjection
	public MemberDetailResult(Long memberId, String name, String nickName, String fileUrl, String memo, int ranking, LocalDate lessonDt, LocalTime lessonStartTime) {
		this.memberId = memberId;
		this.name = name;
		this.nickName = nickName;
		this.fileUrl = fileUrl;
		this.memo = memo;
		this.ranking = ranking;
		this.lessonDt = lessonDt;
		this.lessonStartTime = lessonStartTime;
	}

}
