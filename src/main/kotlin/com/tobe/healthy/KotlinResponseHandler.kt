package com.tobe.healthy

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK

data class KotlinResponseHandler<T>(
    val status: HttpStatus = OK,
    val message: String,
    val data: T
)
