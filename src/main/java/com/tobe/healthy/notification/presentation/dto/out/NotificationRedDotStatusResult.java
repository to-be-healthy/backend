package com.tobe.healthy.notification.presentation.dto.out;

import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.notification.domain.entity.NotificationCategory;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationRedDotStatusResult {

	private NotificationCategory notificationCategory;
	private boolean redDotStatus;

	@QueryProjection
	public NotificationRedDotStatusResult(NotificationCategory notificationCategory, boolean redDotStatus) {
		this.notificationCategory = notificationCategory;
		this.redDotStatus = redDotStatus;
	}
}
