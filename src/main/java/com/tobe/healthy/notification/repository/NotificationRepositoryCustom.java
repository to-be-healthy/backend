package com.tobe.healthy.notification.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tobe.healthy.notification.presentation.dto.out.NotificationRedDotStatusResult;
import com.tobe.healthy.notification.domain.Notification;
import com.tobe.healthy.notification.domain.NotificationCategory;

public interface NotificationRepositoryCustom {

	Page<Notification> findAllByNotificationType(
		NotificationCategory notificationCategory,
		Long receiverId,
		Pageable pageable);

	List<NotificationRedDotStatusResult> findAllRedDotStatus(
		NotificationCategory notificationCategory,
		Long receiverId);

	boolean findRedDotStatus(Long receiverId);
}
