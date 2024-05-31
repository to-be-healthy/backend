package com.tobe.healthy.push.domain.dto.`in`

import jakarta.validation.constraints.NotBlank

data class CommandRegisterToken(
    @field:NotBlank(message = "토큰을 입력해 주세요.")
    val token: String?
)
