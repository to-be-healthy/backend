package com.tobe.healthy.schedule.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReservationStatus implements EnumMapperType {
	COMPLETED("예약완료"),
	AVAILABLE("예약가능"),
	NO_SHOW("노쇼");

	private final String description;

	@Override
	public String getCode() {
		return name();
	}
}
