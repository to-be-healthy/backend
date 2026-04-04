package com.tobe.healthy.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

	Notification findByIdAndReceiverId(Long notificationId, Long receiverId);
}
