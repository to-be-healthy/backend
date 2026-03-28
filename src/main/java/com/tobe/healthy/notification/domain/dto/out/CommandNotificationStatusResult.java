package com.tobe.healthy.notification.domain.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tobe.healthy.notification.domain.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandNotificationStatusResult {

    private Long notificationId;
    @JsonProperty("isRead")
    private boolean isRead;

    public static CommandNotificationStatusResult from(Notification notification) {
        return CommandNotificationStatusResult.builder()
                .notificationId(notification.getId())
                .isRead(notification.isRead())
                .build();
    }
}
