package com.tobe.healthy.diet.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class DietUpdateCommand {

    @Schema(description = "아침 파일")
    private String breakfastFile;

    @Schema(description = "점심 파일")
    private String lunchFile;

    @Schema(description = "저녁 파일")
    private String dinnerFile;

    @Schema(description = "아침 단식 여부" , example = "false")
    private boolean breakfastFast;

    @Schema(description = "점심 단식 여부" , example = "false")
    private boolean lunchFast;

    @Schema(description = "저녁 단식 여부" , example = "false")
    private boolean dinnerFast;

}
