package com.tobe.healthy.lesson_history.domain.dto.`in`

import com.tobe.healthy.lesson_history.domain.dto.out.UploadFileResponse
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "수업 일지 등록 DTO")
data class RegisterLessonHistoryCommand(
    @Schema(description = "등록할 수업 일지 제목", example = "홍길동님 오늘 PT 수업 일지입니다.", required = true)
    @field:NotBlank(message = "제목을 입력해 주세요.")
    val title: String?,

    @Schema(description = "등록할 수업 일지 내용", example = "처음에 왔을 때보다 엄청 성장한 것 같아요! 저도 보람차네요^^", required = true)
    @field:NotBlank(message = "내용을 입력해 주세요.")
    val content: String?,

    @Schema(description = "등록할 학생", example = "1", required = true)
    @field:NotNull(message = "학생 정보를 입력해 주세요.")
    val studentId: Long?,

    @Schema(description = "등록할 일정", example = "1", required = true)
    @field:NotNull(message = "일정 정보를 입력해 주세요.")
    val scheduleId: Long?,

    @Schema(description = "등록할 파일", required = false)
    val uploadFileResponse: MutableList<UploadFileResponse>?
)
