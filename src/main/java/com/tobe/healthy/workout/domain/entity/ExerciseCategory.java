package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExerciseCategory implements EnumMapperType {

    STRENGTH("strength"),
    STRETCHING("stretching"),
    PLYOMETRICS("plyometrics"),
    STRONGMAN("strongman"),
    POWERLIFTING("powerlifting"),
    CARDIO("cardio"),
    OLYMPIC_WEIGHTLIFTING("olympic_weightlifting");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
