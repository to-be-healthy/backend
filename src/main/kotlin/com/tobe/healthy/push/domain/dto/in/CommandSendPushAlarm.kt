package com.tobe.healthy.push.domain.dto.`in`

import jakarta.validation.constraints.NotBlank

data class CommandSendPushAlarm(
    @field:NotBlank(message = "제목을 입력해 주세요.")
    val title: String?,

    @field:NotBlank(message = "메시지를 입력해 주세요.")
    val message: String?,

    @field:NotBlank(message = "토큰을 입력해 주세요.")
    val token: String?
    // todo: 2024-05-07 화요일 오후 14:31 다른 조건으로 사용자를 구분하는 기능이 필요(사용자 이름 또는 id등으로) - seonwoo_jung
)
