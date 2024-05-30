package com.tobe.healthy.schedule.domain.dto.`in`

import jakarta.validation.constraints.NotNull

data class CommandUpdateScheduleStatus(
    @field:NotNull(message = "수업 일정 ID를 입력해 주세요.")
    val scheduleIds: List<Long>?
)
