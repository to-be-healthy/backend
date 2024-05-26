package com.tobe.healthy.diet.domain.dto.in;

import com.tobe.healthy.diet.domain.entity.DietType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class DietAddCommand {

    private DietType type;
    private String file;
    private boolean fast;

}
