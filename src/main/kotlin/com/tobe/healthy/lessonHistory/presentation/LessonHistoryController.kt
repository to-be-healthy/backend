package com.tobe.healthy.lessonHistory.presentation

import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonHistory.application.LessonHistoryService
import com.tobe.healthy.lessonHistory.domain.dto.RegisterLessonHistoryCommand
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lessonhistory/v1")
class LessonHistoryController(
    private val lessonHistoryService: LessonHistoryService
) {

    @PostMapping("/register")
    fun registerLessonHistory(@RequestBody request: RegisterLessonHistoryCommand,
                              @AuthenticationPrincipal member: CustomMemberDetails): Boolean {
        return lessonHistoryService.registerLessonHistory(request, member.memberId)
    }


}