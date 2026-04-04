package com.tobe.healthy.lessonhistory.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WritingStatus {
	WRITTEN("작성완료"),
	UNWRITTEN("미작성");

	private final String description;
}
