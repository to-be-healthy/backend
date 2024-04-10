package com.tobe.healthy.lessonHistory.application

import com.tobe.healthy.lessonHistory.domain.dto.SearchCondRequest
import com.tobe.healthy.lessonHistory.repository.LessonHistoryRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class LessonHistoryServiceKoTest(
    private val lessonHistoryRepository: LessonHistoryRepository
) : StringSpec({
    extensions(SpringExtension)

    "모든 기록을 조회한다." {
        val request = SearchCondRequest(searchDate = null)
    }
})