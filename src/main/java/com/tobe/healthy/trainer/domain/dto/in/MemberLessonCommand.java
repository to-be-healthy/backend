package com.tobe.healthy.trainer.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberLessonCommand {

    @Schema(description = "수업할 PT 횟수" , example = "10")
    @Positive(message = "양수를 입력해주세요.")
    private int lessonCnt;


    @Builder
    public MemberLessonCommand(int lessonCnt) {
        this.lessonCnt = lessonCnt;
    }
}
