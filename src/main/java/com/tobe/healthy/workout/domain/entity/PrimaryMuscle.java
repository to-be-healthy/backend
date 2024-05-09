package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.common.enums.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PrimaryMuscle implements EnumMapperType {

    ABDOMINALS("abdominals"),
    HAMSTRINGS("hamstrings"),
    ADDUCTORS("adductors"),
    QUADRICEPS("quadriceps"),
    BICEPS("biceps"),
    SHOULDERS("shoulders"),
    CHEST("chest"),
    MIDDLE_BACK("middle_back"),
    CALVES("calves"),
    GLUTES("glutes"),
    LOWER_BACK("lower_back"),
    LATS("lats"),
    TRICEPS("triceps"),
    TRAPS("traps"),
    FOREARMS("forearms"),
    NECK("neck"),
    ABDUCTORS("abductors");


    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
