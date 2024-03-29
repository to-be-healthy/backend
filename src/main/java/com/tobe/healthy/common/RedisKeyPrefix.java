package com.tobe.healthy.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RedisKeyPrefix implements EnumMapperType {
    EMAIL_VERIFICATION("verification:"),
    REFRESH_TOKEN("refresh-token:"),
    INVITATION("invitation:");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
