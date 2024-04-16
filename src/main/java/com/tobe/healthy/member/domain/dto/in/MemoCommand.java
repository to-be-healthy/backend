package com.tobe.healthy.member.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemoCommand {

    @Schema(description = "메모내용", example = "메모메모")
    private String memo;

}
