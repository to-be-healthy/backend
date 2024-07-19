package com.tobe.healthy.member.domain.dto.out;

import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.gym.domain.dto.out.GymDto;
import com.tobe.healthy.point.domain.dto.out.PointDto;
import com.tobe.healthy.point.domain.dto.out.RankDto;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@ToString
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
	private PointDto point;
	private RankDto rank;
	private GymDto gym;
	private boolean isNonmember;

	@QueryProjection
	public MemberDetailResult(Long memberId, String name, String nickName, String fileUrl, String memo, int ranking, LocalDate lessonDt, LocalTime lessonStartTime, Long nonMemberId) {
		this.memberId = memberId;
		this.name = name;
		this.nickName = nickName;
		this.fileUrl = fileUrl;
		this.memo = memo;
		this.ranking = ranking;
		this.lessonDt = lessonDt;
		this.lessonStartTime = lessonStartTime;
		this.isNonmember = nonMemberId != null;
	}

}
