package com.tobe.healthy.lessonHistory.presentation

import com.tobe.healthy.KotlinResponseHandler
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonHistory.application.LessonHistoryService
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommandResult
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommentUpdateCommand
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryUpdateCommand
import com.tobe.healthy.lessonHistory.domain.dto.RegisterLessonHistoryCommand
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
                              @RequestPart(required = false) file: MultipartFile,
                              @AuthenticationPrincipal member: CustomMemberDetails): KotlinResponseHandler<Boolean> {
        return KotlinResponseHandler(
            message = "수업 내역을 등록하였습니다.",
            data = lessonHistoryService.registerLessonHistory(request, file, member.memberId)
        )
    }

    @GetMapping
    fun findAllLessonHistory(): KotlinResponseHandler<List<LessonHistoryCommandResult>> {
        return KotlinResponseHandler(
            message = "수업 내역 전체를 조회하였습니다.",
            data = lessonHistoryService.findAllLessonHistory()
        )
    }

    @GetMapping("/{lessonHistoryId}")
    fun findOneLessonHistory(@PathVariable lessonHistoryId: Long): KotlinResponseHandler<LessonHistoryCommandResult> {
        return KotlinResponseHandler(
            message = "수업 내역을 조회하였습니다.",
            data = lessonHistoryService.findOneLessonHistory(lessonHistoryId)
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