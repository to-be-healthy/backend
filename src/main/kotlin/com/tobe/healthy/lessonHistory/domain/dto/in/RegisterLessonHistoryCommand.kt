package com.tobe.healthy.lessonHistory.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "수업 일지 등록 DTO")
data class RegisterLessonHistoryCommand(
    @Schema(description = "등록할 수업 일지 제목")
    @field:NotBlank(message = "제목을 입력해 주세요.")
    val title: String?,

    @Schema(description = "등록할 수업 일지 내용")
    @field:NotBlank(message = "내용을 입력해 주세요.")
    val content: String?,

    @Schema(description = "등록할 학생")
    @field:NotNull(message = "학생 정보를 입력해 주세요.")
    val studentId: Long?,

    @Schema(description = "등록할 일정")
    @field:NotNull(message = "일정 정보를 입력해 주세요.")
    val scheduleId: Long?
)
