package com.tobe.healthy.diet.domain.dto.in;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class DietUpdateCommand {

    private String breakfastFile;
    private String lunchFile;
    private String dinnerFile;
    private boolean breakfastFast;
    private boolean lunchFast;
    private boolean dinnerFast;

}
