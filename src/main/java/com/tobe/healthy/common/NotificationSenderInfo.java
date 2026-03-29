package com.tobe.healthy.common;

import com.tobe.healthy.notification.domain.entity.NotificationSenderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class NotificationSenderInfo {

	private NotificationSenderInfo() {
	}

	public static SenderInfo getSenderInfo() {
		return new SenderInfo();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SenderInfo {

		@Builder.Default
		private String profileUrl = "https://cdn.to-be-healthy.shop/origin/profile/default.png";

		@Builder.Default
		private NotificationSenderType senderType = NotificationSenderType.SYSTEM;
	}
}
