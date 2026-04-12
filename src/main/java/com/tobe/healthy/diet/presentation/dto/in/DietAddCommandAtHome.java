package com.tobe.healthy.diet.presentation.dto.in;

import com.tobe.healthy.diet.domain.DietType;

public record DietAddCommandAtHome(DietType type, String file, boolean fast, String eatDate) {
}
