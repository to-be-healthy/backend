package com.tobe.healthy.push.presentation.dto.in;

import com.tobe.healthy.push.domain.DeviceType;

public record CommandSendPushAlarm(
	String title,
	String message,
	String token,
	String clickUrl,
	DeviceType deviceType
) {
}
