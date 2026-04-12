package com.tobe.healthy.diet.presentation.dto.out;

import java.util.List;

import org.springframework.util.ObjectUtils;

import com.tobe.healthy.member.domain.AlarmStatus;

public record DietUploadDaysResult(AlarmStatus dietNoticeStatus, List<String> uploadDays) {

	public static DietUploadDaysResult create(AlarmStatus dietNoticeStatus, List<String> days) {
		return new DietUploadDaysResult(
			dietNoticeStatus,
			ObjectUtils.isEmpty(days) ? null : days
		);
	}
}
