package com.tobe.healthy

import mu.two.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK

val log = KotlinLogging.logger { }

data class ApiResultResponse<T>(
    val status: HttpStatus = OK,
    val message: String,
    val data: T
)
