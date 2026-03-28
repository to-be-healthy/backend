package com.tobe.healthy.notification.domain.dto.in;

import com.tobe.healthy.notification.domain.entity.NotificationCategory;
import com.tobe.healthy.notification.domain.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandSendNotification {

    private String title;
    private String content;
    private List<Long> receiverIds;
    private NotificationType notificationType;
    private NotificationCategory notificationCategory;
    private Long targetId;
    private String clickUrl;
    private Long studentId;
    private String studentName;
}
