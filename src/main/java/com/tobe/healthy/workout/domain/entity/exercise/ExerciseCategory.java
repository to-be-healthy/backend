package com.tobe.healthy.workout.domain.entity.exercise;

import com.tobe.healthy.common.enums.EnumMapperType;
import com.tobe.healthy.workout.domain.dto.out.ExerciseCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum ExerciseCategory implements EnumMapperType {

    CORE("코어"),
    LEG("다리"),
    ARM("팔"),
    SHOULDER("어깨"),
    CHEST("가슴"),
    BACK("등"),
    TRAPEZIUS("승모근"),
    STRETCHING("스트레칭");


    private final String description;

    @Override
    public String getCode() {
        return name();
    }

    public static List<ExerciseCategoryDto> getCategoryList(){
        return Arrays.stream(ExerciseCategory.values()).map(ExerciseCategoryDto::from).toList();
    }
}
