package com.tobe.healthy.diet.domain.dto.in;

import com.tobe.healthy.workout.domain.dto.in.RegisterFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class DietUpdateCommand {

    private String breakfastFile;
    private String lunchFile;
    private String dinnerFile;
    private boolean breakfastFast;
    private boolean lunchFast;
    private boolean dinnerFast;

}
