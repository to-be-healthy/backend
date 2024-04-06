package com.tobe.healthy.point.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PointType implements EnumMapperType {
    NO_SHOW("수업불참"),
    WORKOUT("운동기록"),
    DIET("식단기록");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
