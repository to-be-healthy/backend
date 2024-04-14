package com.tobe.healthy.lessonHistory.presentation

import com.tobe.healthy.KotlinResponseHandler
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonHistory.application.LessonHistoryService
import com.tobe.healthy.lessonHistory.domain.dto.CommentRegisterCommand
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommandResult
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommentUpdateCommand
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryUpdateCommand
import com.tobe.healthy.lessonHistory.domain.dto.RegisterLessonHistoryCommand
import com.tobe.healthy.lessonHistory.domain.dto.SearchCondRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/lessonhistory/v1")
class LessonHistoryController(
    private val lessonHistoryService: LessonHistoryService
) {

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun registerLessonHistory(@RequestPart @Valid request: RegisterLessonHistoryCommand,
                              @RequestPart(required = false) uploadFiles: MutableList<MultipartFile>?,
                              @AuthenticationPrincipal member: CustomMemberDetails): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "수업 내역을 등록하였습니다.",
            data = lessonHistoryService.registerLessonHistory(request, uploadFiles, member.memberId)
        )
    }

    @GetMapping
    fun findAllLessonHistory(request: SearchCondRequest,
                             pageable: Pageable,
                             @AuthenticationPrincipal member: CustomMemberDetails): KotlinResponseHandler<Page<LessonHistoryCommandResult>> {
        return KotlinResponseHandler(
            message = "수업 내역 전체를 조회하였습니다.",
            data = lessonHistoryService.findAllLessonHistory(request, pageable, member.memberId, member.memberType)
        )
    }

    @GetMapping("/{lessonHistoryId}")
    fun findOneLessonHistory(@PathVariable lessonHistoryId: Long,
                             @AuthenticationPrincipal member: CustomMemberDetails): KotlinResponseHandler<List<LessonHistoryCommandResult>> {
        return KotlinResponseHandler(
            message = "수업 내역을 조회하였습니다.",
            data = lessonHistoryService.findOneLessonHistory(lessonHistoryId, member.memberId, member.memberType)
        )
    }

    @PatchMapping("/{lessonHistoryId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun updateLessonHistory(@PathVariable lessonHistoryId: Long,
                            @RequestBody lessonHistoryUpdateCommand: LessonHistoryUpdateCommand): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "수업 내역이 수정되었습니다.",
            data = lessonHistoryService.updateLessonHistory(lessonHistoryId, lessonHistoryUpdateCommand)
        )
    }

    @PatchMapping("/comment/{lessonHistoryCommentId}")
    fun updateLessonHistoryComment(@PathVariable lessonHistoryCommentId: Long,
                                   @RequestBody @Valid request: LessonHistoryCommentUpdateCommand): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "댓글이 수정되었습니다.",
            data = lessonHistoryService.updateLessonHistoryComment(lessonHistoryCommentId, request)
        )
    }

    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @DeleteMapping("/{lessonHistoryId}")
    fun deleteLessonHistory(@PathVariable lessonHistoryId: Long): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "수업 내역이 삭제되었습니다.",
            data = lessonHistoryService.deleteLessonHistory(lessonHistoryId)
        )
    }

    @DeleteMapping("/comment/{lessonHistoryCommentId}")
    fun deleteLessonHistoryComment(@PathVariable lessonHistoryCommentId: Long): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "댓글이 삭제되었습니다.",
            data = lessonHistoryService.deleteLessonHistoryComment(lessonHistoryCommentId)
        )
    }

    @PostMapping("/comment/{lessonHistoryId}")
    fun registerLessonHistoryComment(@PathVariable lessonHistoryId: Long,
                                     @RequestPart @Valid request: CommentRegisterCommand,
                                     @RequestPart(required = false) uploadFiles: MutableList<MultipartFile>?,
                                     @AuthenticationPrincipal member: CustomMemberDetails): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "댓글이 등록되었습니다.",
            data = lessonHistoryService.registerLessonHistoryComment(lessonHistoryId, uploadFiles, request, member.memberId)
        )
    }

    @PostMapping("/{lessonHistoryId}/comment/{lessonHistoryCommentId}")
    fun registerLessonHistoryComment(@PathVariable lessonHistoryId: Long,
                                     @PathVariable lessonHistoryCommentId: Long,
                                     @RequestPart @Valid request: CommentRegisterCommand,
                                     @RequestPart(required = false) uploadFiles: MutableList<MultipartFile>?,
                                     @AuthenticationPrincipal member: CustomMemberDetails): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "대댓글이 등록되었습니다.",
            data = lessonHistoryService.registerLessonHistoryReply(lessonHistoryId, lessonHistoryCommentId, uploadFiles, request, member.memberId)
        )
    }
}
