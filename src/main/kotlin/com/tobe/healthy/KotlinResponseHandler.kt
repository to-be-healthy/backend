package com.tobe.healthy

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK

val log = KotlinLogging.logger { }

data class KotlinResponseHandler<T>(
    val status: HttpStatus = OK,
    val message: String,
    val data: T
)
