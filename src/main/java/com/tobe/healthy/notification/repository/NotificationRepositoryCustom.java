package com.tobe.healthy.notification.repository;

import com.tobe.healthy.notification.domain.dto.out.NotificationRedDotStatusResult;
import com.tobe.healthy.notification.domain.entity.Notification;
import com.tobe.healthy.notification.domain.entity.NotificationCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
