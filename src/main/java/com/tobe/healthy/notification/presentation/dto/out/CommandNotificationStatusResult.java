package com.tobe.healthy.notification.presentation.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tobe.healthy.notification.domain.Notification;

public record CommandNotificationStatusResult(
	Long notificationId,
	@JsonProperty("isRead") boolean isRead
) {
	public static CommandNotificationStatusResult from(Notification notification) {
		return new CommandNotificationStatusResult(notification.getId(), notification.isRead());
	}
}
