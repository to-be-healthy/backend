package com.tobe.healthy.schedule.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReserveType implements EnumMapperType {
	TRUE("예약완료"),
	FALSE("예약가능");

	private final String description;

	@Override
	public String getCode() {
		return name();
	}
}
