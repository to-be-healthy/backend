package com.tobe.healthy.diet.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class DietAddCommand extends DietUpdateCommand{

    @Schema(description = "먹은 날짜" , example = "2024-05-28")
    @NotEmpty(message = "날짜를 입력해 주세요.")
    private String eatDate;

}
