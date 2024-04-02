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

    @Schema(description = "헬스장 이용권 시작날짜")
    private LocalDate gymStartDt;

    @Schema(description = "헬스장 이용권 종료날짜")
    private LocalDate gymEndDt;

    @Builder
    public MemberLessonCommand(int lessonCnt, LocalDate gymStartDt, LocalDate gymEndDt) {
        this.lessonCnt = lessonCnt;
        this.gymStartDt = gymStartDt;
        this.gymEndDt = gymEndDt;
    }
}
