package com.tobe.healthy.common.event;

import lombok.Getter;

@Getter
public class CustomEvent<T> {
    private EventType type;
    private T result;

    public CustomEvent(T result, EventType type) {
        this.result = result;
        this.type = type;
    }
}