package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberType implements EnumMapperType {
    MEMBER("회원"),
    TRAINER("트레이너");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
