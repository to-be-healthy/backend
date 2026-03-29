package com.tobe.healthy.point.presentation.dto.out;

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
public class RankDto {

	private int ranking;
	private int lastMonthRanking;
	private int totalMemberCnt;

	public static RankDto create(int ranking, int lastMonthRanking, int totalMemberCnt) {
		return RankDto.builder()
			.ranking(ranking)
			.lastMonthRanking(lastMonthRanking)
			.totalMemberCnt(totalMemberCnt)
			.build();
	}
}
