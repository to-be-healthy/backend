package com.tobe.healthy.notification.presentation.dto.out;

import java.util.List;
import java.util.stream.Collectors;

import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.notification.domain.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandSendNotificationResult {

	private Long notificationId;
	private String title;
	private String content;
	private List<NotificationReciverInfo> receivers;

	public static CommandSendNotificationResult from(List<Notification> notifications) {
		Notification first = notifications.isEmpty() ? null : notifications.get(0);
		return CommandSendNotificationResult.builder()
			.notificationId(first != null ? first.getId() : null)
			.title(first != null ? first.getTitle() : null)
			.content(first != null ? first.getContent() : null)
			.receivers(notifications.stream()
				.map(n -> NotificationReciverInfo.from(n.getReceiver()))
				.collect(Collectors.toList()))
			.build();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NotificationReciverInfo {

		private Long receiverId;
		private String receiverName;

		public static NotificationReciverInfo from(Member receiver) {
			return NotificationReciverInfo.builder()
				.receiverId(receiver != null ? receiver.getId() : null)
				.receiverName(receiver != null ? receiver.getName() : null)
				.build();
		}
	}
}
