package com.tobe.healthy.notification.repository;

import com.tobe.healthy.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

    Notification findByIdAndReceiverId(Long notificationId, Long receiverId);
}
