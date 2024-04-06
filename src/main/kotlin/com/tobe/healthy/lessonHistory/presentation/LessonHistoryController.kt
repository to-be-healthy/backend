package com.tobe.healthy.lessonHistory.presentation

import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonHistory.application.LessonHistoryService
import com.tobe.healthy.lessonHistory.domain.dto.RegisterLessonHistoryCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lessonhistory/v1")
@Tag(name = "07. 수업 내역 API", description = "수업 내역을 조회하는 API")
class LessonHistoryController(
    private val lessonHistoryService: LessonHistoryService
) {
    @Operation(
        summary = "트레이너가 수업 내역 등록",
        responses = [
            ApiResponse(responseCode = "200", description = "수업 내역이 등록되었습니다."),
            ApiResponse(responseCode = "404", description = "학생을 찾을 수 없습니다."),
            ApiResponse(responseCode = "404", description = "트레이너를 찾을 수 없습니다."),
            ApiResponse(responseCode = "404", description = "일정을 찾을 수 없습니다.")
        ]
    )
    @PostMapping("/register")
    fun registerLessonHistory(@RequestBody request: RegisterLessonHistoryCommand,
                              @AuthenticationPrincipal member: CustomMemberDetails): Boolean {
        return lessonHistoryService.registerLessonHistory(request, member.memberId)
    }
}