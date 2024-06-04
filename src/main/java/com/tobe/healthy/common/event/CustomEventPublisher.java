package com.tobe.healthy.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomEventPublisher<T> {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(final T result, final EventType type) {
        System.out.println("Publishing generic event.");
        CustomEvent<T> genericEvent = new CustomEvent<>(result, type);
        applicationEventPublisher.publishEvent(genericEvent);
    }
}
