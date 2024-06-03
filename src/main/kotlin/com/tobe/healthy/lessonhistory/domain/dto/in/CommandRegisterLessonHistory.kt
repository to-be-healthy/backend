package com.tobe.healthy.lessonhistory.domain.dto.`in`

import com.tobe.healthy.lessonhistory.domain.dto.out.CommandUploadFileResult
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "수업 일지 등록 DTO")
data class CommandRegisterLessonHistory(
    @Schema(description = "등록할 수업 일지 제목", example = "홍길동님 오늘 PT 수업 일지입니다.", required = true)
    val title: String,

    @Schema(description = "등록할 수업 일지 내용", example = "처음에 왔을 때보다 엄청 성장한 것 같아요! 저도 보람차네요^^", required = true)
    val content: String,

    @Schema(description = "등록할 학생 ID", example = "1", required = true)
    val studentId: Long,

    @Schema(description = "등록할 일정 ID", example = "1", required = true)
    val scheduleId: Long,

    @Schema(description = "등록할 파일", required = false)
    val uploadFiles: MutableList<CommandUploadFileResult> = mutableListOf()
)
