package com.tobe.healthy.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {

    SCHEDULE_CANCEL("트레이너 또는 학생이 수업을 취소한 경우");

    private final String description;

}
