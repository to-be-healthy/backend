package com.tobe.healthy.trainer.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MemberInviteCommand {

    @Schema(description = "이름" , example = "임채린")
    @NotEmpty(message = "회원 이름을 추가해 주세요.")
    private String name;

    @Schema(description = "수업할 PT 횟수" , example = "10")
    @Positive(message = "양수를 입력해주세요.")
    private int lessonNum;

    @Schema(description = "나이" , example = "20")
    private int age = 0;

    @Schema(description = "키" , example = "180")
    private int height = 0;

    @Schema(description = "몸무게" , example = "60")
    private int weight = 0;


}
