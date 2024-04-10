package com.tobe.healthy.point.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Calculation implements EnumMapperType {
    PLUS("+"),
    MINUS("-");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
