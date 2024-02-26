package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Alarm implements EnumMapperType {
    ABLE("알림켜기"),
    DISABLE("알림끄기");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}