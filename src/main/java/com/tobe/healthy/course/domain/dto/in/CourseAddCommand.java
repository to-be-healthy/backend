package com.tobe.healthy.course.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CourseAddCommand {

    @Schema(description = "학생 ID" , example = "1")
    @NotNull
    private Long memberId;

    @Schema(description = "수업할 PT 횟수" , example = "10")
    @Positive(message = "양수를 입력해주세요.")
    private int lessonCnt;

    public static CourseAddCommand create(Long memberId, int lessonCnt) {
        return CourseAddCommand.builder()
                .memberId(memberId)
                .lessonCnt(lessonCnt)
                .build();
    }
}
