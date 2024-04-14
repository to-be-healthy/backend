package com.tobe.healthy.course.domain.dto.in;

import com.tobe.healthy.course.domain.entity.CourseHistoryType;
import com.tobe.healthy.point.domain.entity.Calculation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CourseUpdateCommand {

    @Schema(description = "학생 ID" , example = "1")
    @NotNull
    private Long memberId;

    @Schema(description = "추가 및 증감" , example = "PLUS, MINUS")
    @NotNull
    private Calculation calculation;

    @Schema(description = "증감 타입" , example = "COURSE_CREATE(수강권 생성), PLUS_ONE_LESSON(1회 수강권 지급), RESERVATION(수업 예약), RESERVATION_CANCEL(수업 예약 취소)")
    @NotNull
    private CourseHistoryType type;

    @Schema(description = "추가 및 차감 할 횟수" , example = "10")
    @Positive(message = "양수를 입력해주세요.")
    private int updateCnt;

}
