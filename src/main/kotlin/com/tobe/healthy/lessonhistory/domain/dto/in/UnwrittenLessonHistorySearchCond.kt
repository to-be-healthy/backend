package com.tobe.healthy.lessonhistory.domain.dto.`in`

import com.tobe.healthy.lessonhistory.domain.entity.WritingStatus

data class UnwrittenLessonHistorySearchCond(
    val lessonDate: String? = null,
    val studentId: Long? = null,
    val writingStatus: WritingStatus? = null
)