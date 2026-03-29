package com.tobe.healthy.diet.presentation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.member.presentation.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietDto {

	private Long dietId;
	private MemberDto member;
	private Long likeCnt;
	private Long commentCnt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDate eatDate;
	private boolean liked;
	private boolean feedbackChecked;

	@Builder.Default
	private DietDetailDto breakfast = new DietDetailDto();
	@Builder.Default
	private DietDetailDto lunch = new DietDetailDto();
	@Builder.Default
	private DietDetailDto dinner = new DietDetailDto();

	@QueryProjection
	public DietDto(Long dietId, Member member, boolean liked, Long likeCnt, Long commentCnt, LocalDate eatDate,
		boolean fastBreakfast, boolean fastLunch, boolean fastDinner) {
		this.dietId = dietId;
		this.member = MemberDto.from(member);
		this.liked = liked;
		this.likeCnt = likeCnt;
		this.commentCnt = commentCnt;
		this.eatDate = eatDate;
		this.breakfast = new DietDetailDto(fastBreakfast);
		this.lunch = new DietDetailDto(fastLunch);
		this.dinner = new DietDetailDto(fastDinner);
	}

	public static DietDto from(Diet diet) {
		DietDto dto = DietDto.builder()
			.dietId(diet.getDietId())
			.member(MemberDto.from(diet.getMember()))
			.likeCnt(diet.getLikeCnt())
			.commentCnt(diet.getCommentCnt())
			.createdAt(diet.getCreatedAt())
			.updatedAt(diet.getUpdatedAt())
			.eatDate(diet.getEatDate())
			.build();
		dto.breakfast.setFast(diet.getFastBreakfast());
		dto.lunch.setFast(diet.getFastLunch());
		dto.dinner.setFast(diet.getFastDinner());
		return dto;
	}

	public void setDietFiles(List<DietFileDto> filesDto) {
		for (DietFileDto file : filesDto) {
			switch (file.getType()) {
				case BREAKFAST -> this.breakfast.setDietFile(file);
				case LUNCH -> this.lunch.setDietFile(file);
				case DINNER -> this.dinner.setDietFile(file);
			}
		}
	}

}
