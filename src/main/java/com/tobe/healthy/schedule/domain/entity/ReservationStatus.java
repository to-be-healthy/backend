package com.tobe.healthy.schedule.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReservationStatus implements EnumMapperType {
	COMPLETED("예약완료"),
	AVAILABLE("예약가능"),
	NO_SHOW("노쇼"),
	LUNCH_TIME("점심시간"),
	CLOSED_DAY("휴무");

	private final String description;

	@Override
	public String getCode() {
		return name();
	}
}
