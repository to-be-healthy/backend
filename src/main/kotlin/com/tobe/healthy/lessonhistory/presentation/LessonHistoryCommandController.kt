package com.tobe.healthy.lessonhistory.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.error.ErrorResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.application.LessonHistoryCommandService
import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommandRegisterComment
import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommandRegisterLessonHistory
import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommandUpdateComment
import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommandUpdateLessonHistory
import com.tobe.healthy.lessonhistory.domain.dto.out.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/lessonhistory/v1")
@Tag(name = "07. 수업 일지")
class LessonHistoryCommandController(
    private val lessonHistoryCommandService: LessonHistoryCommandService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @Operation(summary = "수업 일지를 등록한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "수업 일지를 등록하였습니다."),
            ApiResponse(responseCode = "404(1)", description = "학생을 찾을 수 없습니다.", content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "404(2)", description = "트레이너를 찾을 수 없습니다.", content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "404(3)", description = "일정을 찾을 수 없습니다.", content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))]),
        ])
    fun registerLessonHistory(
        @RequestBody @Valid request: CommandRegisterLessonHistory,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<CommandRegisterLessonHistoryResult> {
        return ApiResultResponse(
            message = "수업 일지를 등록하였습니다.",
            data = lessonHistoryCommandService.registerLessonHistory(request, member.memberId)
        )
    }

    @Operation(summary = "게시글/댓글 작성 전에 파일을 첨부한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "게시글/댓글 작성 전에 파일을 첨부한다.")
        ])
    @PostMapping("/file")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun registerFilesOfLessonHistory(
        uploadFiles: MutableList<MultipartFile>,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<List<CommandUploadFileResult>> {
        return ApiResultResponse(
            message = "파일을 등록하였습니다.",
            data = lessonHistoryCommandService.registerFilesOfLessonHistory(uploadFiles, member.memberId)
        )
    }

    @Operation(summary = "수업일지를 수정한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "수업 일지를 수정하였습니다."),
            ApiResponse(responseCode = "404", description = "수업 일지를 찾을 수 없습니다."),
        ])
    @PatchMapping("/{lessonHistoryId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun updateLessonHistory(
        @PathVariable lessonHistoryId: Long,
        @RequestBody @Valid request: CommandUpdateLessonHistory,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<CommandUpdateLessonHistoryResult> {
        return ApiResultResponse(
            message = "수업 일지가 수정되었습니다.",
            data = lessonHistoryCommandService.updateLessonHistory(lessonHistoryId, request)
        )
    }

    @Operation(summary = "수업일지를 삭제한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "수업 일지를 삭제하였습니다."),
            ApiResponse(responseCode = "404", description = "수업 일지를 찾을 수 없습니다."),
        ])
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @DeleteMapping("/{lessonHistoryId}")
    fun deleteLessonHistory(
        @PathVariable lessonHistoryId: Long
    ): ApiResultResponse<Long> {
        return ApiResultResponse(
            message = "수업 일지가 삭제되었습니다.",
            data = lessonHistoryCommandService.deleteLessonHistory(lessonHistoryId)
        )
    }

    @Operation(summary = "수업일지에 댓글을 등록한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "수업 일지에 댓글이 등록되었습니다."),
            ApiResponse(responseCode = "404(1)", description = "회원을 찾을 수 없습니다."),
            ApiResponse(responseCode = "404(2)", description = "수업 일지를 찾을 수 없습니다."),
        ])
    @PostMapping("/{lessonHistoryId}/comment")
    fun registerLessonHistoryComment(
        @PathVariable lessonHistoryId: Long,
        @RequestBody @Valid request: CommandRegisterComment,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<CommandRegisterCommentResult> {
        return ApiResultResponse(
            message = "댓글이 등록되었습니다.",
            data = lessonHistoryCommandService.registerLessonHistoryComment(lessonHistoryId, request, member.memberId)
        )
    }

    @Operation(summary = "수업일지에 답글을 등록한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "수업 일지에 대댓글이 등록되었습니다."),
            ApiResponse(responseCode = "404(1)", description = "회원을 찾을 수 없습니다."),
            ApiResponse(responseCode = "404(2)", description = "수업 일지를 찾을 수 없습니다."),
        ])
    @PostMapping("/{lessonHistoryId}/comment/{lessonHistoryCommentId}")
    fun registerLessonHistoryComment(
        @PathVariable lessonHistoryId: Long,
        @PathVariable lessonHistoryCommentId: Long,
        @RequestBody @Valid request: CommandRegisterComment,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<CommandRegisterReplyResult> {
        return ApiResultResponse(
            message = "대댓글이 등록되었습니다.",
            data = lessonHistoryCommandService.registerLessonHistoryReply(lessonHistoryId, lessonHistoryCommentId, request, member.memberId)
        )
    }

    @Operation(summary = "수업일지에 댓글/답글을 수정한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "수업 일지에 댓글이 수정되었습니다."),
            ApiResponse(responseCode = "404", description = "수업 일지에 댓글을 찾을 수 없습니다.")
        ])
    @PatchMapping("/comment/{lessonHistoryCommentId}")
    fun updateLessonHistoryComment(
        @PathVariable lessonHistoryCommentId: Long,
        @RequestBody @Valid request: CommandUpdateComment
    ): ApiResultResponse<CommandUpdateCommentResult> {
        return ApiResultResponse(
            message = "댓글이 수정되었습니다.",
            data = lessonHistoryCommandService.updateLessonHistoryComment(lessonHistoryCommentId, request)
        )
    }

    @Operation(summary = "수업일지에 댓글/답글을 삭제한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "수업 일지에 댓글이 삭제되었습니다."),
            ApiResponse(responseCode = "404", description = "수업 일지에 댓글을 찾을 수 없습니다.")
        ])
    @DeleteMapping("/comment/{lessonHistoryCommentId}")
    fun deleteLessonHistoryComment(
        @PathVariable lessonHistoryCommentId: Long
    ): ApiResultResponse<Long> {
        return ApiResultResponse(
            message = "댓글 1개가 삭제되었습니다.",
            data = lessonHistoryCommandService.deleteLessonHistoryComment(lessonHistoryCommentId)
        )
    }
}