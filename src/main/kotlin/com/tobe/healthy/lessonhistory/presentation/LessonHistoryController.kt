package com.tobe.healthy.lessonhistory.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.application.LessonHistoryService
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RetrieveLessonHistoryByDateCond
import com.tobe.healthy.lessonhistory.domain.dto.`in`.UnwrittenLessonHistorySearchCond
import com.tobe.healthy.lessonhistory.domain.dto.out.CustomRetrieveLessonHistoryByDateCondResult
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryDetailResult
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveUnwrittenLessonHistory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lessonhistory/v1")
@Tag(name = "07. 수업 일지")
class LessonHistoryController(
    private val lessonHistoryService: LessonHistoryService
) {

    @Operation(summary = "전체 수업 일지를 조회한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "전체 수업 일지를 조회하였습니다.")
        ])
    @GetMapping
    fun findAllLessonHistory(
        @ParameterObject request: RetrieveLessonHistoryByDateCond,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<CustomRetrieveLessonHistoryByDateCondResult> {
        return ApiResultResponse(
            message = "전체 수업 일지를 조회하였습니다.",
            data = lessonHistoryService.findAllLessonHistory(request, member)
        )
    }

    @Operation(summary = "수업일지 단건을 조회한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "학생의 전체 수업 일지를 조회하였습니다."),
            ApiResponse(responseCode = "404", description = "수업 일지를 찾을 수 없습니다."),
        ])
    @GetMapping("/{lessonHistoryId}")
    fun findOneLessonHistory(
        @PathVariable lessonHistoryId: Long,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<RetrieveLessonHistoryDetailResult?> {
        return ApiResultResponse(
            message = "수업 일지 단건을 조회하였습니다.",
            data = lessonHistoryService.findOneLessonHistory(lessonHistoryId, member)
        )
    }

    @Operation(summary = "학생의 수업일지 전체를 조회한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "학생의 전체 수업 일지를 조회하였습니다.")
        ])
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun findAllLessonHistoryByMemberId(
        @PathVariable studentId: Long,
        @ParameterObject request: RetrieveLessonHistoryByDateCond,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<CustomRetrieveLessonHistoryByDateCondResult> {
        return ApiResultResponse(
            message = "학생의 수업 일지 전체를 조회하였습니다.",
            data = lessonHistoryService.findAllLessonHistoryByMemberId(studentId, request, member)
        )
    }

    @Operation(summary = "수업일지를 작성하지 않은 수업들을 조회하였습니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "수업일지를 작성하지 않은 수업들을 조회하였습니다."),
        ])
    @GetMapping("/unwritten")
    fun findAllUnwrittenLessonHistory(
        request: UnwrittenLessonHistorySearchCond,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<List<RetrieveUnwrittenLessonHistory>> {
        return ApiResultResponse(
            message = "수업일지를 작성하지 않은 수업들을 조회하였습니다.",
            data = lessonHistoryService.findAllUnwrittenLessonHistory(request, member.memberId)
        )
    }
}