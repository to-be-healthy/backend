package com.tobe.healthy.push.presentation.dto.in;

import com.tobe.healthy.push.domain.DeviceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandSendPushAlarm {

	private String title;
	private String message;
	private String token;
	private String clickUrl;
	private DeviceType deviceType;
}
