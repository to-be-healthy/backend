package com.tobe.healthy.diet.domain.dto.in;

import com.tobe.healthy.diet.domain.entity.DietType;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class DietAddCommand {

    private DietType type;
    private String file;
    private boolean fast;

}
