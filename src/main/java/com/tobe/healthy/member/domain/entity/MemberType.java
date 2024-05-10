package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.enums.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberType implements EnumMapperType {
    STUDENT("학생"),
    TRAINER("트레이너");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
