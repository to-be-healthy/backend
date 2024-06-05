package com.tobe.healthy.common.event;

import lombok.Getter;

@Getter
public record CustomEvent<T>(T result, EventType type) {
}