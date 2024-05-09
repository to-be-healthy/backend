package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.enums.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AlarmStatus implements EnumMapperType {
    ENABLED("알림켜기"),
    DISABLE("알림끄기");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}