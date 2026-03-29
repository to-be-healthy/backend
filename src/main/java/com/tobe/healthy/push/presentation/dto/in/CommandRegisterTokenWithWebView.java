package com.tobe.healthy.push.presentation.dto.in;

import com.tobe.healthy.push.domain.entity.DeviceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRegisterTokenWithWebView {

	private Long memberId;
	private String token;
	private DeviceType deviceType;
}
