package com.tobe.healthy.point.presentation.dto.out;

public record PointDto(
	String searchDate,
	int monthPoint,
	int totalPoint
) {
	public static PointDto create(String searchDate, int monthPoint, int totalPoint) {
		return new PointDto(searchDate, monthPoint, totalPoint);
	}
}
