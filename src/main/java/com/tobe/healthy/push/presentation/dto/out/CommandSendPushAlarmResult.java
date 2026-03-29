package com.tobe.healthy.push.presentation.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandSendPushAlarmResult {

	private String title;
	private String message;

	public static CommandSendPushAlarmResult from(String title, String message) {
		return CommandSendPushAlarmResult.builder()
			.title(title)
			.message(message)
			.build();
	}
}
