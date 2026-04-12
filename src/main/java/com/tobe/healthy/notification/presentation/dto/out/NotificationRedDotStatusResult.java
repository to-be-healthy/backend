package com.tobe.healthy.notification.presentation.dto.out;

import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.notification.domain.NotificationCategory;

public record NotificationRedDotStatusResult(
	NotificationCategory notificationCategory,
	boolean redDotStatus
) {
	@QueryProjection
	public NotificationRedDotStatusResult {
	}
}
