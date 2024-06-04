package com.tobe.healthy.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomEventPublisher<T> {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(final T result, final EventType type) {
        log.info("Publishing generic event.");
        CustomEvent<T> genericEvent = new CustomEvent<>(result, type);
        applicationEventPublisher.publishEvent(genericEvent);
    }
}
