package com.tobe.healthy.lessonHistory.domain.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RegisterLessonHistoryCommand(
    @field:NotBlank(message = "제목을 입력해 주세요.")
    val title: String?,

    @field:NotBlank(message = "내용을 입력해 주세요.")
    val content: String?,

    @field:NotNull(message = "학생 정보를 입력해 주세요.")
    val studentId: Long?,

    @field:NotNull(message = "일정 정보를 입력해 주세요.")
    val scheduleId: Long?
)
