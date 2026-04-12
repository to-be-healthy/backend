package com.tobe.healthy.notification.presentation.dto.in;

import java.util.List;

import com.tobe.healthy.notification.domain.NotificationCategory;
import com.tobe.healthy.notification.domain.NotificationType;

public record CommandSendNotification(
	String title,
	String content,
	List<Long> receiverIds,
	NotificationType notificationType,
	NotificationCategory notificationCategory,
	Long targetId,
	String clickUrl,
	Long studentId,
	String studentName
) {
}
