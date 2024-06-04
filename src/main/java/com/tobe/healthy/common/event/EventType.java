package com.tobe.healthy.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {

    SCHEDULE_CANCEL("트레이너 또는 학생이 수업을 취소한 경우"),
    NOTIFICATION_RESERVE("학생이 수업을 신청한 경우 알림 전송");

    private final String description;

}
