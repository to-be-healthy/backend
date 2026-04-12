package com.tobe.healthy.push.presentation.dto.in;

import com.tobe.healthy.push.domain.DeviceType;

public record CommandRegisterTokenWithWebView(
	Long memberId,
	String token,
	DeviceType deviceType
) {
}
