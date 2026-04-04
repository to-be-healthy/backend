package com.tobe.healthy.notification.presentation.dto.out;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tobe.healthy.notification.domain.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveNotificationWithRedDotResult {

	private List<RetrieveNotificationResult> content;
	private List<NotificationRedDotStatusResult> redDotStatus;

	public static RetrieveNotificationWithRedDotResult from(
		Page<Notification> notifications,
		List<NotificationRedDotStatusResult> redDotStatus) {
		return RetrieveNotificationWithRedDotResult.builder()
			.content(notifications.getContent().stream()
				.map(RetrieveNotificationResult::from)
				.collect(Collectors.toList()))
			.redDotStatus(redDotStatus)
			.build();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RetrieveNotificationResult {

		private Long notificationId;
		private String notificationCategoryAndType;
		private Long receiverId;
		private String receiverName;
		private String title;
		private String content;
		private String createdAt;
		@JsonProperty("isRead")
		private boolean isRead;
		private Long targetId;
		private Long studentId;
		private String studentName;

		public static RetrieveNotificationResult from(Notification notification) {
			return RetrieveNotificationResult.builder()
				.notificationId(notification.getId())
				.notificationCategoryAndType(
					notification.getNotificationCategory().name() + "-" + notification.getNotificationType().name())
				.receiverId(notification.getReceiver() != null ? notification.getReceiver().getId() : null)
				.receiverName(notification.getReceiver() != null ? notification.getReceiver().getName() : null)
				.title(notification.getTitle())
				.content(notification.getContent())
				.createdAt(notification.getCreatedAt().toString())
				.isRead(notification.isRead())
				.targetId(notification.getTargetId())
				.studentId(notification.getStudentId())
				.studentName(notification.getStudentName())
				.build();
		}
	}
}
