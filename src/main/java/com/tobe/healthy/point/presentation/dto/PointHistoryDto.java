package com.tobe.healthy.point.presentation.dto;

import java.time.LocalDateTime;

import com.tobe.healthy.point.domain.Calculation;
import com.tobe.healthy.point.domain.Point;
import com.tobe.healthy.point.domain.PointType;

public record PointHistoryDto(
	Long pointId,
	PointType type,
	Calculation calculation,
	int point,
	LocalDateTime createdAt
) {
	public static PointHistoryDto from(Point point) {
		return new PointHistoryDto(
			point.getPointId(),
			point.getType(),
			point.getCalculation(),
			point.getPoint(),
			point.getCreatedAt()
		);
	}
}
