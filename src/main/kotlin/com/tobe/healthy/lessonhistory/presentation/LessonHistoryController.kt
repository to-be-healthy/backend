package com.tobe.healthy.lessonhistory.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.common.CustomPagingResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.application.LessonHistoryService
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RetrieveLessonHistoryByDateCond
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryByDateCondResult
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryDetailResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
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
    fun findAllLessonHistory(@Parameter(content = [Content(schema = Schema(implementation = RetrieveLessonHistoryByDateCond::class))]) request: RetrieveLessonHistoryByDateCond,
                             @ParameterObject pageable: Pageable,
                             @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<CustomPagingResponse<RetrieveLessonHistoryByDateCondResult>> {
        return ApiResultResponse(
            message = "전체 수업 일지를 조회하였습니다.",
            data = lessonHistoryService.findAllLessonHistory(request, pageable, member.memberId, member.memberType)
        )
    }

    @Operation(summary = "학생의 수업일지 전체를 조회한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "학생의 전체 수업 일지를 조회하였습니다.")
        ])
    @GetMapping("/detail/{studentId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun findAllLessonHistoryByMemberId(@Parameter(description = "학생 ID", example = "1") @PathVariable studentId: Long,
                                       @Parameter(content = [Content(schema = Schema(implementation = RetrieveLessonHistoryByDateCond::class))]) request: RetrieveLessonHistoryByDateCond,
                                       @ParameterObject pageable: Pageable): ApiResultResponse<CustomPagingResponse<RetrieveLessonHistoryByDateCondResult>> {
        return ApiResultResponse(
            message = "학생의 수업 일지 전체를 조회하였습니다.",
            data = lessonHistoryService.findAllLessonHistoryByMemberId(studentId, request, pageable)
        )
    }

    @Operation(summary = "수업일지 단건을 조회한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "학생의 전체 수업 일지를 조회하였습니다."),
            ApiResponse(responseCode = "404", description = "수업 일지를 찾을 수 없습니다."),
        ])
    @GetMapping("/{lessonHistoryId}")
    fun findOneLessonHistory(@Parameter(description = "수업일지 ID", example = "1") @PathVariable lessonHistoryId: Long,
                             @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<RetrieveLessonHistoryDetailResult?> {
        return ApiResultResponse(
            message = "수업 일지 단건을 조회하였습니다.",
            data = lessonHistoryService.findOneLessonHistory(lessonHistoryId, member.memberId, member.memberType)
        )
    }
}
