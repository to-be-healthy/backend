package com.tobe.healthy.lessonhistory.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LessonHistoryReadStatus {
	READ("읽음"),
	UNREAD("안읽음");

	private final String description;
}
