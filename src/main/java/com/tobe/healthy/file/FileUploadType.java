package com.tobe.healthy.file;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileUploadType implements EnumMapperType {
	LESSON_HISTORY("수업 기록"),
	LESSON_HISTORY_COMMENT("수업 기록 댓글");

	private final String description;

	@Override
	public String getCode() {
		return name();
	}
}
