package com.tobe.healthy.point.presentation.dto;

public record TempRankDto(
	int ranking,
	Long memberId,
	int pointSum
) {
}
