package com.tobe.healthy.schedule.domain.dto.out

import com.querydsl.core.annotations.QueryProjection

data class FeedbackNotificationToTrainer @QueryProjection constructor(
    val trainerId: Long,
    val count: Long
)
