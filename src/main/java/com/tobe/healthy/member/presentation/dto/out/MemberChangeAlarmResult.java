package com.tobe.healthy.member.presentation.dto.out;

import com.tobe.healthy.member.domain.AlarmStatus;
import com.tobe.healthy.member.domain.AlarmType;

public record MemberChangeAlarmResult(
	String type,
	AlarmStatus status
) {

	public static MemberChangeAlarmResult from(AlarmType type, AlarmStatus status) {
		return new MemberChangeAlarmResult(
			type.getDescription(),
			status
		);
	}
}
