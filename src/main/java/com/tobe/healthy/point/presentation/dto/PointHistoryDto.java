package com.tobe.healthy.point.presentation.dto;

import java.time.LocalDateTime;

import com.tobe.healthy.point.domain.Calculation;
import com.tobe.healthy.point.domain.Point;
import com.tobe.healthy.point.domain.PointType;

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
public class PointHistoryDto {

	private Long pointId;
	private PointType type;
	private Calculation calculation;
	private int point;
	private LocalDateTime createdAt;

	public static PointHistoryDto from(Point point) {
		return PointHistoryDto.builder()
			.pointId(point.getPointId())
			.type(point.getType())
			.calculation(point.getCalculation())
			.point(point.getPoint())
			.createdAt(point.getCreatedAt())
			.build();
	}

}
