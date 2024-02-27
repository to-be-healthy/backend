package com.tobe.healthy.common;

import lombok.ToString;

@ToString
public class EnumMapperValue {

	private String code;
	private String description;

	public EnumMapperValue(EnumMapperType enumMapperType) {
		this.code = enumMapperType.getCode();
		this.description = enumMapperType.getDescription();
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
