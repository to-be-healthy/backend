package com.tobe.healthy.diet.presentation.dto.out;

import java.util.List;

import org.springframework.util.ObjectUtils;

import com.tobe.healthy.member.domain.AlarmStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DietUploadDaysResult {

	private AlarmStatus dietNoticeStatus;
	@Builder.Default
	private List<String> uploadDays = null;

	public static DietUploadDaysResult create(AlarmStatus dietNoticeStatus, List<String> days) {
		return DietUploadDaysResult.builder()
			.dietNoticeStatus(dietNoticeStatus)
			.uploadDays(ObjectUtils.isEmpty(days) ? null : days)
			.build();
	}
}
