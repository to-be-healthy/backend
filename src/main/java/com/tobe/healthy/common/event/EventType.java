package com.tobe.healthy.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {

    SCHEDULE_CANCEL_BY_STUDENT("학생이 수업을 취소한 경우"),
    SCHEDULE_CANCEL_BY_TRAINER("트레이너가 학생의 수업을 취소한 경우");

    private final String description;

}
