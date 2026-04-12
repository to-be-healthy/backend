package com.tobe.healthy.push.presentation.dto.in;

public record CommandSendPushAlarmToMember(
	String title,
	String message
) {
}
