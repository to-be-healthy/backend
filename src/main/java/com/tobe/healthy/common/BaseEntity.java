package com.tobe.healthy.common;

import org.springframework.data.domain.AbstractAggregateRoot;

public abstract class BaseEntity<ENTITY, ID> extends AbstractAggregateRoot<BaseEntity<ENTITY, ID>> {
}
