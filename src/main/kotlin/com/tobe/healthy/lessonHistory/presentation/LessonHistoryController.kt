package com.tobe.healthy.lessonHistory.presentation

import com.tobe.healthy.KotlinResponseHandler
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonHistory.application.LessonHistoryService
import com.tobe.healthy.lessonHistory.domain.dto.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/lessonhistory/v1")
class LessonHistoryController(
    private val lessonHistoryService: LessonHistoryService
) {

    @PostMapping("/register")
    fun registerLessonHistory(@RequestPart request: RegisterLessonHistoryCommand,
                              @RequestPart(required = false) uploadFiles: MutableList<MultipartFile>,
                              @AuthenticationPrincipal member: CustomMemberDetails): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "수업 내역을 등록하였습니다.",
            data = lessonHistoryService.registerLessonHistory(request, uploadFiles, member.memberId)
        )
    }

    // todo: "속한 팀의 정보만 조회하는 기능 추가하기"
    @GetMapping
    fun findAllLessonHistory(request: SearchCondRequest,
                             @AuthenticationPrincipal member: CustomMemberDetails): KotlinResponseHandler<List<LessonHistoryCommandResult>> {
        return KotlinResponseHandler(
            message = "수업 내역 전체를 조회하였습니다.",
            data = lessonHistoryService.findAllLessonHistory(request, member.memberId)
        )
    }

    @GetMapping("/{lessonHistoryId}")
    fun findOneLessonHistory(@PathVariable lessonHistoryId: Long,
                             @AuthenticationPrincipal member: CustomMemberDetails): KotlinResponseHandler<List<LessonHistoryCommandResult>> {
        return KotlinResponseHandler(
            message = "수업 내역을 조회하였습니다.",
            data = lessonHistoryService.findOneLessonHistory(lessonHistoryId, member.memberId)
        )
    }

    @PatchMapping("/{lessonHistoryId}")
    fun updateLessonHistory(@PathVariable lessonHistoryId: Long,
                            @RequestBody lessonHistoryUpdateCommand: LessonHistoryUpdateCommand): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "수업 내역이 수정되었습니다.",
            data = lessonHistoryService.updateLessonHistory(lessonHistoryId, lessonHistoryUpdateCommand)
        )
    }

    @PatchMapping("/comment/{lessonHistoryCommentId}")
    fun updateLessonHistoryComment(@PathVariable lessonHistoryCommentId: Long,
                                   @RequestBody request: LessonHistoryCommentUpdateCommand): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "댓글이 수정되었습니다.",
            data = lessonHistoryService.updateLessonHistoryComment(lessonHistoryCommentId, request)
        )
    }

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
}