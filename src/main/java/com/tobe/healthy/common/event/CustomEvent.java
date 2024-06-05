package com.tobe.healthy.common.event;

public record CustomEvent<T>(T result, EventType type) {
}