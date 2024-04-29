package com.tobe.healthy.diet.domain.dto.in;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class DietUpdateCommand {

    private MultipartFile breakfastFile;
    private MultipartFile lunchFile;
    private MultipartFile dinnerFile;
    private boolean breakfastFast;
    private boolean lunchFast;
    private boolean dinnerFast;

}
